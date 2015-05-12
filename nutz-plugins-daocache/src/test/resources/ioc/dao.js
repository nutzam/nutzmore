	var ioc = {
		dataSource : {
			type : "com.alibaba.druid.pool.DruidDataSource",
	       events : {
	       	create : "init",
	          depose : 'close'
	       },
	       fields : {
	          url : "jdbc:mysql://127.0.0.1:3306/nutzbook",
	          username : "root",
	          password : "root",
	          testWhileIdle : true,
	          validationQuery : "select 1",
	          maxActive : 100,
	          filters : "mergeStat",
	          connectionProperties : "druid.stat.slowSqlMillis=1000"
	        }
		}
		dao : {
			type : "org.nutz.dao.impl.NutDaoExt",
			args : [{refer:"dataSource"}],
			fields : {
				executor : {refer:"cacheExecutor"}
			}
		},
		cacheExecutor : {
			type : "org.nutz.plugins.cache.dao.CachedNutDaoExecutor",
			fields : {
				cacheProvider : {refer:"cacheProvider"},
				cachedTableNames : ["tb_user", "tb_user_profile"], // 需要缓存的表,
				enableWhenTrans : false, // 事务作用域内不启用缓存,默认也是false
				db : "MYSQL"
			}
		},
		cacheProvider : {
			type : "org.nutz.plugins.cache.dao.impl.provider.MemoryDaoCacheProvider",
			fields : {
				cacheSize : 10000000
			},
			events : {
				create : "init"
			}
		}
	};