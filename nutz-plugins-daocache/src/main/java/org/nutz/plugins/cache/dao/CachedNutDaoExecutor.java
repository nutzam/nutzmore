package org.nutz.plugins.cache.dao;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.nutz.dao.DB;
import org.nutz.dao.DaoException;
import org.nutz.dao.impl.sql.run.NutDaoExecutor;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.api.DaoCacheProvider;
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
	
	protected boolean cache4Null = true;

	/**
	 * <b>数据库类型</b>,当前仅支持 MYSQL, ORACLE, PSQL, 默认MYSQL
	 */
    protected DB db = DB.MYSQL;

    private static final Log log = Logs.get();

	public void exec(Connection conn, DaoStatement st) {
		String prepSql = st.toPreparedStatement();
		if (prepSql == null) {
			super.exec(conn, st);
			return;
		}
		SQLStatementParser parser = sqlParser(prepSql);
		List<SQLStatement> statementList = null;
		try {
            statementList = parser.parseStatementList();
        }
        catch (Exception e) {
            log.debug("parser SQL sql, skip cache detect!! SQL=" + prepSql);
            super.exec(conn, st);
            return;
        }
		if (statementList.size() != 1) {
			log.warn("more than one sql in one DaoStatement!! skip cache detect!! SQL=" + prepSql);
			super.exec(conn, st);
			return;
		}
		SQLStatement sqlStatement = statementList.get(0);
		if (sqlStatement == null) {
			log.warn("can't parse SQL !! skip cache detect!! SQL=" + prepSql);
			super.exec(conn, st);
			return;
		}
		// 检查需要执行的sql
		NSqlAdapter adapter = new NSqlAdapter();
		sqlStatement.accept(adapter); // 得到将会操作的表
		List<String> tableNames = adapter.tableNames;
		if (DEBUG)
		    log.debug("sql = " + prepSql + ", tables = " + tableNames);
		if (sqlStatement instanceof SQLSelectStatement) {
			// 如果是select且不是batch(参数表只有一行,那么可能是缓存哦)
			Object[][] params = st.getParamMatrix();
			if (Trans.isTransactionNone() || enableWhenTrans) {
			    if (tableNames.size() == 1 && isCache4Table(tableNames.get(0)) && params.length <= 1) {
			        String tableName = tableNames.get(0);
			        String key = genKey(prepSql, params);
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
			            super.exec(conn, st);
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
		    if (mark != null && (Boolean)mark)
		        tableNames.clear();
		}
		try {
			super.exec(conn, st);
		} finally {
			try {
				if (!tableNames.isEmpty()) {
					for (String tableName : tableNames) {
					    if (DEBUG)
					        log.debug("Clear Cache=" + tableName);
					    getCacheProvider().clear(genCacheName(tableName));
					}
				}
			} catch (Throwable e) {
				log.warn("clear cache fail: " + tableNames, e);
			}
		}
	}

	/**
	 * 缓存key的生成机制,默认是 sha1(sql):sha1('_'.join(params))
	 * <p/>子类可覆盖本方法实现更有效的key生成
	 */
	protected String genKey(String prepareSql, Object[][] params) {
		String args = "_";
		if (params != null && params.length > 0) {
			args = Json.toJson(params[0], JsonFormat.full().setIndent(0));
		}
		return Lang.sha1(prepareSql) + ":" + Lang.sha1(args);
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
			return new MySqlStatementParser(sql);
		case ORACLE:
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
	            || (cachedTableNamePatten != null && cachedTableNamePatten.matcher(tableName).find());
	}
	
	public DaoCacheProvider getCacheProvider() {
	    if (cacheProvider == null)
	        throw new IllegalArgumentException("Need CacheProvider!!");
        return cacheProvider;
    }
	
	public void setCache4Null(boolean cache4Null) {
        this.cache4Null = cache4Null;
    }
}
