Nutz的Dao层插件
==================================

适用性: 1.b.38以上的版本理论上可用, 1.b.50以上的版本测试过

当前实现的局限性
-------------------

* 单表缓存,多表的查询不缓存
* 基于druid的语法分析器,所以当前仅支持mysql,oracle,pgsql
* 请充分了解缓存对数据实时性的影响!!!

示例配置
-------------------

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
		},
		dao : {
		        // NutDaoExt是为了兼容1.b.52及以下版本, 53版或以上请直接用NutDao 
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
				cachedTableNames : ["tb_user", "tb_user_profile"], // 需要缓存的表
				enableWhenTrans : false, // 事务作用域内是否启用,默认false
				cache4Null : true // 是否缓存空值,默认true
			}
		},
		cacheProvider : {
			type : "org.nutz.plugins.cache.dao.impl.provider.MemoryDaoCacheProvider",
			fields : {
				cacheSize : 10000
			},
			events : {
				create : "init"
			}
		}
	};
