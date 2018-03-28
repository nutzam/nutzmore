Nutz的Dao层插件
==================================

简介(可用性:生产,维护者:wendal)
==================================

为NutDao提供缓存支持,SQL级别的缓存

适用性: 1.b.38以上的版本理论上可用, 1.b.50以上的版本测试过


当前实现的局限性
-------------------

* 单表缓存,多表的查询不缓存
* 基于druid的语法分析器,所以当前仅支持mysql,oracle,pgsql
* 请充分了解缓存对数据实时性的影响!!!

示例配置
-------------------

*1.r.58及以上请使用interceptors注入,1.r.57.r3及以下使用executor方式注入*

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
				//executor : {refer:"cacheExecutor"} // executor是1.r.57.r3及以下版本的写法.
				interceptors : [{refer:"cacheExecutor"}, "log"] // 这是1.r.58及以上的写法
			}
		},
		cacheExecutor : {
			type : "org.nutz.plugins.cache.dao.DaoCacheInterceptor",
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
	
Ehcache示例配置
-------------------

推荐分2个文件配置, 第一个是ehcache.js, 请务必留意里面的说明

与Shiro一起使用不是必须条件, daocache与shiro的联系只是共享一个CacheManager实例,这是可选的

为shiro和daocache分配不同的CacheManager实例是完全可以,请注意区分场景!!

```js
var ioc = {
		cacheManager : {
			type : "net.sf.ehcache.CacheManager",
			factory : "net.sf.ehcache.CacheManager#getCacheManager",
			args : ["nutzbook"] // 对应shiro.ini中指定的ehcache.xml中定义的name
		}
		/*      
		// 如果不需要shiro初始化的Ehcache, 使用下面的方式配置
		cacheManager : {
			type : "net.sf.ehcache.CacheManager",
			factory : "net.sf.ehcache.CacheManager#create" // 这是工厂方法的强大之处
		}
		 */
};
```

第二个是dao.js, 区别只是cacheProvider指向的类不一样

```js
var ioc = {
		conf : {
			type : "org.nutz.ioc.impl.PropertiesProxy",
			fields : {
				paths : ["custom/"]
			}
		},
	    dataSource : {
	        type : "com.alibaba.druid.pool.DruidDataSource",
	        events : {
	        	create : "init",
	            depose : 'close'
	        },
	        fields : {
	            url : {java:"$conf.get('db.url')"},
	            username : {java:"$conf.get('db.username')"},
	            password : {java:"$conf.get('db.password')"},
	            testWhileIdle : true,
	            validationQuery : {java:"$conf.get('db.validationQuery')"},
	            maxActive : {java:"$conf.get('db.maxActive')"},
	            filters : "mergeStat",
	            connectionProperties : "druid.stat.slowSqlMillis=2000",
	        }
	    },
		dao : {
			type : "org.nutz.dao.impl.NutDaoExt",
			args : [{refer:"dataSource"}],
			fields : {
				//executor : {refer:"cacheExecutor"} // executor是1.r.57.r3及以下版本的写法.
				interceptors : [{refer:"cacheExecutor"}, "log"] // 这是1.r.58及以上的写法
			}
		},
		cacheExecutor : {
			type : "org.nutz.plugins.cache.dao.DaoCacheInterceptor", // 1.r.57.r3及以下版本用 CachedNutDaoExecutor
			fields : {
				cacheProvider : {refer:"cacheProvider"},
				cachedTableNames : [
				                    "t_user_profile",
				                    "t_user", "t_role", "t_permission", "t_role_permission",
				                    "t_topic", "t_topic_reply",
				                    "t_oauth_user"
				                    ]
			}
		},
		// 基于Ehcache的DaoCacheProvider
		cacheProvider : {
			type : "org.nutz.plugins.cache.dao.impl.provider.EhcacheDaoCacheProvider",
			fields : {
				cacheManager : {refer:"cacheManager"} // 引用ehcache.js中定义的CacheManager
			},
			events : {
				create : "init"
			}
		}
};
```

有用户反映ehcache在shiro.ini的配置顺序会导致获取到CacheManager为null,请确保
ehcache的声明在其他所有realm声明之前

## 使用redis

dao.js中的cacheProvider变更一下

```js
		// 基于Ehcache的DaoCacheProvider
		cacheProvider : {
			type : "org.nutz.plugins.cache.dao.impl.provider.RedisDaoCacheProvider",
			fields : {
				jedisPool : {refer:"jedisPool"} // 引用nutz-integration-jedis的JedisPool
			},
			events : {
				create : "init"
			}
		}
```