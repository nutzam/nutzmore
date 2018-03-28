var ioc = {
	conf : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : [ "custom/" ]
		}
	},
	dataSource : {
		type : "com.alibaba.druid.pool.DruidDataSource",
		events : {
			depose : 'close'
		},
		fields : {
			driverClassName : {
				java : "$conf.get('db.driverClassName')"
			},
			url : {
				java : "$conf.get('db.url')"
			},
			maxWait : {
				java : "$conf.get('db.maxWait')"
			},
			defaultAutoCommit : {
				java : "$conf.get('db.defaultAutoCommit')"
			},
			validationQuery : {
				java : "$conf.get('db.validationQuery')"
			},
			testWhileIdle : {
				java : "$conf.get('db.testWhileIdle')"
			},
			maxActive : {
				java : "$conf.get('db.maxActive')"
			},
			filters : {
				java : "$conf.get('db.filters')"
			},
			connectionProperties : {
				java : "$conf.get('db.connectionProperties')"
			},
			username : {
				java : "$conf.get('db.username')"
			},
			password : {
				java : "$conf.get('db.password')"
			},
			timeBetweenEvictionRunsMillis : {
				java : "$conf.get('db.timeBetweenEvictionRunsMillis')"
			},
			minEvictableIdleTimeMillis : {
				java : "$conf.get('db.minEvictableIdleTimeMillis')"
			},
			poolPreparedStatements : {
				java : "$conf.get('db.poolPreparedStatements')"
			},
			maxPoolPreparedStatementPerConnectionSize : {
				java : "$conf.get('db.maxPoolPreparedStatementPerConnectionSize')"
			},
			initialSize : {
				java : "$conf.get('db.initialSize')"
			},
			minIdle : {
				java : "$conf.get('db.minIdle')"
			},
			testWhileIdle : {
				java : "$conf.get('db.testWhileIdle')"
			},
			testOnBorrow : {
				java : "$conf.get('db.testOnBorrow')"
			},
			testOnReturn : {
				java : "$conf.get('db.testOnReturn')"
			},
			removeAbandoned : {
				java : "$conf.get('db.removeAbandoned')"
			},
			removeAbandonedTimeout : {
				java : "$conf.get('db.removeAbandonedTimeout')"
			},
			logAbandoned : {
				java : "$conf.get('db.logAbandoned')"
			},
		}
	},
	dao : {
		type : "org.nutz.dao.impl.NutDao",
		args : [ {
			refer : "dataSource"
		} ]
	}
};