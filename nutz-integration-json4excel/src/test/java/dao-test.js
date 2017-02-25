var ioc = {
	config : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : ["dao-test.properties"]
		}
	},
	dataSource : {
		type :"com.alibaba.druid.pool.DruidDataSource",
		events : {
			depose :"close"
		},
		fields : {
			driverClassName : {
				java :"$config.get('driver')"
			},
			url : {
				java :"$config.get('url')"
			},
			username : {
				java :"$config.get('username')"
			},
			password : {
				java :"$config.get('password')"
			},
			maxWait : 15000
		}
	},
	dao : {
		type :"org.nutz.dao.impl.NutDao",
		args : [ {refer :"dataSource"}]
	}
}
