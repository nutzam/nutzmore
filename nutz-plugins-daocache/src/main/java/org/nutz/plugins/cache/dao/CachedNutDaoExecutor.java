package org.nutz.plugins.cache.dao;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nutz.dao.DB;
import org.nutz.dao.DaoException;
import org.nutz.dao.impl.sql.run.NutDaoExecutor;
import org.nutz.dao.sql.DaoStatement;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.impl.NopDaoCacheProvider;
import org.nutz.trans.Trans;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class CachedNutDaoExecutor extends NutDaoExecutor {

	protected DaoCacheProvider cacheProvider;

	protected boolean enableWhenTrans;
	
	protected String cacheClearMark = "dao-cache-clear";
	
	protected Set<String> cachedTableNames = new HashSet<String>();
	
	public CachedNutDaoExecutor() {
		cacheProvider = new NopDaoCacheProvider();
	}

	public void exec(Connection conn, DaoStatement st) {
		String prepSql = st.toPreparedStatement();
		if (prepSql == null) {
			super.exec(conn, st);
			return;
		}
		SQLStatementParser parser = sqlParser(prepSql);
		List<SQLStatement> statementList = parser.parseStatementList();
		if (statementList.size() != 1) {
			log.warn("more than one sql in one DaoStatement!! skip cache detect!!");
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
		log.debug("sql = " + prepSql + ", tables = " + tableNames);
		if (sqlStatement instanceof SQLSelectStatement) {
			// 如果是select且不是batch(参数表只有一行,那么可能是缓存哦)
			Object[][] params = st.getParamMatrix();
			if (Trans.isTransactionNone() || enableWhenTrans) {
			    if (tableNames.size() == 1 && cachedTableNames.contains(tableNames.get(0)) && params.length <= 1) {
			        String tableName = tableNames.get(0);
			        String key = genKey(prepSql, params);
			        Object cachedValue = cacheProvider.get(tableName, key);
			        if (cachedValue != null) {
			            log.debug("cache found key=" + key);
			            st.getContext().setResult(cachedValue);
			        } else {
			            super.exec(conn, st);
			            cachedValue = st.getContext().getResult();
			            cacheProvider.put(tableName, key, cachedValue);
			        }
			        return;
			    }
			}
			tableNames.clear(); // Select的表可别清除了
		} else {
		    Object mark = st.getContext().attr(cacheClearMark);
		    if (mark == null || (Boolean)mark)
		        tableNames.clear();
		}
		try {
			super.exec(conn, st);
		} finally {
			try {
				if (!tableNames.isEmpty()) {
					for (String tableName : tableNames) {
						cacheProvider.clear(tableName);
					}
				}
			} catch (Throwable e) {
				log.warn("clear cache fail: " + tableNames, e);
			}
		}
	}

	protected String genKey(String prepareSql, Object[][] params) {
		String args = "_";
		if (params != null && params.length > 0) {
			args = Json.toJson(params[0], JsonFormat.full().setIndent(0));
		}
		return Lang.sha1(prepareSql).substring(0, 8) + ":" + Lang.sha1(args);
	}

	private static final Log log = Logs.get();

	protected DB db = DB.MYSQL;

	protected SQLStatementParser sqlParser(String sql) {
		switch (db) {
		case MYSQL:
			return new MySqlStatementParser(sql);
		case ORACLE:
			return new OracleStatementParser(sql);
		case PSQL:
			return new PGSQLStatementParser(sql);
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
}
