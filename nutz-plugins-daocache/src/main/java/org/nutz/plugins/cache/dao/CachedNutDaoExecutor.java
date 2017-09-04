package org.nutz.plugins.cache.dao;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.nutz.dao.DB;
import org.nutz.dao.DaoException;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.impl.sql.run.NutDaoExecutor;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.api.DaoCacheProvider;
import org.nutz.plugins.cache.dao.impl.adapter.XDb2SqlAdapter2;
import org.nutz.plugins.cache.dao.impl.adapter.XMySqlSqlAdapter;
import org.nutz.plugins.cache.dao.impl.adapter.XOracleSqlAdapter;
import org.nutz.plugins.cache.dao.impl.adapter.XPgSqlAdapter;
import org.nutz.plugins.cache.dao.impl.adapter.XSqlServerSqlAdapter;
import org.nutz.trans.Trans;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.db2.parser.DB2StatementParser;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

/**
 * 基于sql的缓存DaoExecutor. 使用Druid的sql处理器. 要配置需要缓存的表及数据库类型!!!!
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class CachedNutDaoExecutor extends NutDaoExecutor {

    /**
     * 缓存实现提供者,默认是MemoryDaoCacheProvider
     */
    protected DaoCacheProvider cacheProvider;

    /**
     * 在事务环境下是否启动,默认禁用
     */
    protected boolean enableWhenTrans;

    /**
     * 禁止清除缓存的标志,在Sql.getContext()中配置
     */
    protected String cacheClearMark = "dao-cache-clear";
    
    /**
     * 跳过缓存的标记
     */
    public static final String CacheSkipMark = "dao-cache-skip";

    /**
     * 需要缓存的数据库表
     */
    protected Set<String> cachedTableNames = new HashSet<String>();

    /**
     * 需要缓存的数据库表名称的正则表达式
     */
    protected Pattern cachedTableNamePatten;

    /**
     * 是否打印详细的log,默认为关
     */
    public static boolean DEBUG = false;

    /**
     * 是否缓存空值
     */
    protected boolean cache4Null = true;

    /**
     * 是否启用,全局开关,默认为true
     */
    protected boolean enable = true;

    /**
     * <b>数据库类型</b>,当前仅支持 MYSQL, ORACLE, PSQL, 默认MYSQL
     */
    protected DB db = DB.MYSQL;

    private static final Log log = Logs.get();

    public void exec(Connection conn, DaoStatement st) {
        if (!enable || ("true".equals(st.getContext().attr(CacheSkipMark)))) {
            _exec(conn, st);
            return;
        }
        String prepSql = st.toPreparedStatement();
        if (prepSql == null) {
            _exec(conn, st);
            return;
        }
        SQLStatementParser parser = sqlParser(prepSql);
        List<SQLStatement> statementList = null;
        try {
            statementList = parser.parseStatementList();
        }
        catch (Exception e) {
            log.debug("Exception when parser SQL sql, skip cache detect!! SQL=" + prepSql);
            _exec(conn, st);
            return;
        }
        if (statementList.size() != 1) {
            log.warn("more than one sql in one DaoStatement!! skip cache detect!! SQL=" + prepSql);
            _exec(conn, st);
            return;
        }
        SQLStatement sqlStatement = statementList.get(0);
        if (sqlStatement == null) {
            log.warn("can't parse SQL !! skip cache detect!! SQL=" + prepSql);
            _exec(conn, st);
            return;
        }
        // 检查需要执行的sql
        XSqlAdapter adapter;
        switch (db) {
        case ORACLE:
        case DM:
            adapter = new XOracleSqlAdapter();
            break;
        case DB2:
            adapter = new XDb2SqlAdapter2();
            break;
        case PSQL:
            adapter = new XPgSqlAdapter();
            break;
        case SQLSERVER:
            adapter = new XSqlServerSqlAdapter();
            break;
        default:
            adapter = new XMySqlSqlAdapter();
            break;
        }
        sqlStatement.accept(adapter); // 得到将会操作的表
        List<String> tableNames = adapter.getTableNames();
        if (DEBUG)
            log.debug("sql = " + prepSql + ", tables = " + tableNames);
        if (sqlStatement instanceof SQLSelectStatement) {
            // 如果是select且不是batch(参数表只有一行,那么可能是缓存哦)
            Object[][] params = st.getParamMatrix();
            if (Trans.isTransactionNone() || enableWhenTrans) {
                if (tableNames.size() == 1
                    && isCache4Table(tableNames.get(0))
                    && params.length <= 1) {
                    String tableName = tableNames.get(0);
                    String key = genKey(st, prepSql, params);
                    if (DEBUG)
                        log.debugf("KEY=%s SQL=%s", key, prepSql);
                    Object cachedValue = getCacheProvider().get(genCacheName(tableName), key);
                    if (cachedValue != null && !(CacheResult.NOT_FOUNT.equals(cachedValue))) {
                        if (CacheResult.NULL.equals(cachedValue))
                            cachedValue = null;
                        if (DEBUG)
                            log.debug("cache found key=" + key);
                        st.getContext().setResult(cachedValue);
                    } else {
                        if (DEBUG)
                            log.debug("cache miss = " + prepSql);
                        _exec(conn, st);
                        cachedValue = st.getContext().getResult();
                        if (cachedValue != null || cache4Null)
                            getCacheProvider().put(genCacheName(tableName), key, cachedValue);
                    }
                    return;
                } else {
                    if (DEBUG)
                        log.debug("not good for cache >> " + prepSql);
                }
            }
            tableNames.clear(); // Select的表可别清除了
        } else {
            Object mark = st.getContext().attr(cacheClearMark);
            if (mark != null && (Boolean) mark)
                tableNames.clear();
        }
        try {
            _exec(conn, st);
        }
        finally {
            try {
                if (!tableNames.isEmpty()) {
                    for (String tableName : tableNames) {
                        if (DEBUG)
                            log.debug("Clear Cache=" + tableName);
                        getCacheProvider().clear(genCacheName(tableName));
                    }
                }
            }
            catch (Throwable e) {
                log.warn("clear cache fail: " + tableNames, e);
            }
        }
    }

    /**
     * 缓存key的生成机制,默认是 hash:pagerNum:pagerSize:sql:param1:param2:....
     * <p/>
     * 子类可覆盖本方法实现更有效的key生成
     */
    protected String genKey(DaoStatement st, String prepareSql, Object[][] params) {
        StringBuilder sb = new StringBuilder();
        long hash = prepareSql.hashCode();
        Pager pager = st.getContext().getPager();
        if (pager != null) {
            sb.append("" + pager.getPageNumber() + ":" + pager.getPageSize() + ":");
        } else {
            sb.append("_:_:");
        }
        sb.append(prepareSql);
        if (params != null && params.length > 0 && params[0].length > 0) {
            for (Object param : params[0]) {
                String v = String.valueOf(param);
                sb.append(":").append(v);
                hash += v.hashCode();
            }
        }

        return hash + ":" + sb.toString();
    }

    /**
     * 生成特定Cache名. 子类可覆盖实现所需要的Cache名
     */
    protected String genCacheName(String tableName) {
        return tableName;
    }

    /**
     * 根据数据库类型解析sql
     */
    protected SQLStatementParser sqlParser(String sql) {
        switch (db) {
        case MYSQL:
        case SQLITE:
            return new MySqlStatementParser(sql);
        case ORACLE:
        case DM:
            return new OracleStatementParser(sql);
        case PSQL:
            return new PGSQLStatementParser(sql);
        case SQLSERVER:
            return new SQLServerStatementParser(sql);
        case DB2:
            return new DB2StatementParser(sql);
        default:
            throw new DaoException("daocache not support at this database");
        }
    }

    public void setCacheProvider(DaoCacheProvider cacheProvider) {
        this.cacheProvider = cacheProvider;
    }

    public void setEnableWhenTrans(boolean enableWhenTrans) {
        this.enableWhenTrans = enableWhenTrans;
    }

    public void setCachedTableNames(Set<String> cachedTableNames) {
        this.cachedTableNames = cachedTableNames;
    }

    public void addCachedTableName(String name) {
        this.cachedTableNames.add(name);
    }

    public void setCachedTableNamePatten(Pattern cachedTableNamePatten) {
        this.cachedTableNamePatten = cachedTableNamePatten;
    }

    public void setCachedTableNamePatten(String cachedTableNamePatten) {
        if (cachedTableNamePatten == null)
            this.cachedTableNamePatten = null;
        else
            this.cachedTableNamePatten = Pattern.compile(cachedTableNamePatten);
    }

    /**
     * 是否对表进行缓存. 子类可以扩展该方法实现更复杂的配置
     */
    protected boolean isCache4Table(String tableName) {
        return this.cachedTableNames.contains(tableName)
               || (cachedTableNamePatten != null
                   && cachedTableNamePatten.matcher(tableName).find());
    }

    public DaoCacheProvider getCacheProvider() {
        if (cacheProvider == null)
            throw new IllegalArgumentException("Need CacheProvider!!");
        return cacheProvider;
    }

    public void setCache4Null(boolean cache4Null) {
        this.cache4Null = cache4Null;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
        if (!this.enable) {
            log.info("CachedNutDaoExecutor will disable.");
        }
    }
    
    public void setMeta(DatabaseMeta meta) {
        this.db = meta.getType();
    }
    
    protected void _exec(Connection conn, DaoStatement st) {
        super.exec(conn, st);
    }
}
