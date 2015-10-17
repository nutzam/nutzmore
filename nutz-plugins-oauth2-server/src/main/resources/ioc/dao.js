var ioc = {
	conf : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : "conf.properties"
		}
	},
	dataSource : {
		type : "com.alibaba.druid.pool.DruidDataSource",
		events : {
			create : "init",
			depose : 'close'
		},
		fields : {
			url : {
				java : "$conf.get('db.url', 'jdbc:mysql://127.0.0.1:3306/oauth2?useUnicode=true&characterEncoding=utf-8')"
			},
			username : {
				java : "$conf.get('db.username', 'root')"
			},
			password : {
				java : "$conf.get('db.password', 'root')"
			},
			maxActive : {
				java : "$conf.getInt('db.maxActive', 20)"
			},
			validationQuery : "SELECT 'x'",
			testWhileIdle : true,
			testOnBorrow : false,
			testOnReturn : false,
			filters : "mergeStat",
			connectionProperties : "druid.stat.slowSqlMillis=1000"
		}
	},

	dao : {
		type : "org.nutz.dao.impl.NutDao",
		args : [ {
			refer : "dataSource"
		} ]
	}
};