Nutz集成AutoLoadCache的插件
======================

简介(可用性:生产,维护者:Rekoe)
==================================

深度集成AutoLoadCache

使用方法
-------------------------

* 在MainModule的IocBy中引用AutoLoadCacheIocLoader


示例IocBy配置
----------------------------------------------
```java
	@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/",
										   "*anno", "net.wendal.nutzbook",
										   "*org.nutz.integration.autoloadcache.AutoLoadCacheIocLoader"})
										   								   
```

redis-pool.js文件配置的爲Redis的緩存
若需要使用内存模式请修改加载类 

```
com.jarvis.cache.redis.ShardedCachePointCut 为  com.jarvis.cache.map.CachePointCut

```

```js
var ioc = {
	jedisPoolConfig : {
		type : "redis.clients.jedis.JedisPoolConfig",
		fields : {
			testWhileIdle : true,
			maxTotal : 100
		}
	},
	shardedJedisPool : {
		type : "redis.clients.jedis.ShardedJedisPool",
		args : [ {
			refer : "jedisPoolConfig"
		}, [ {
			type : "redis.clients.jedis.JedisShardInfo",
			args : [ {
				java : "$conf.get('redis.host', 'localhost')"
			}, 6379, "instance:01" ]
		} ] ],
		events : {
			depose : "destroy"
		}
	},
	autoLoadConfig : {
		type : "com.jarvis.cache.to.AutoLoadConfig",
		fields : {
			threadCnt : 10,
			maxElement : 20000,
			printSlowLog : true,
			slowLoadTime : 500,
			sortType : 1,
			checkFromCacheBeforeLoad : true,
			autoLoadPeriod : 50
		}
	},
	fastjsonSerializer : {
		type : "com.jarvis.cache.serializer.FastjsonSerializer"
	},
	scriptParser : {
        type : "com.jarvis.cache.script.OgnlParser"
    },
	cachePointCut : {
		type : "com.jarvis.cache.map.CachePointCut",
		args : [ {
			refer : "autoLoadConfig",
		},{
		  refer : "fastjsonSerializer"
		} ,{
          refer : "scriptParser"
        }],
		fields : {
			namespace : 'test_hessian',
			needPersist : false
		},
		events : {
		    create : "start",
			depose : "destroy"
		}
	}
};
```

放置到dao.js中, 因为一般来说都有dao.js,而且dao这个bean通常也需要conf配置信息

示例来至nutzbook

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
	            connectionProperties : "druid.stat.slowSqlMillis=2000"
	        }
	    },
		dao : {
			type : "org.nutz.dao.impl.NutDao",
			args : [{refer:"dataSource"}]
		}
};
```

具体使用方法请参考
https://github.com/qiujiayu/AutoLoadCache

# cache-example
---------------------------------------------

[自动加载缓存代码](https://github.com/qiujiayu/AutoLoadCache)
	
后台管理cache入口 需要在web.xml中增加

```xml
<servlet>
		<servlet-name>cacheadmin</servlet-name>
		<servlet-class>com.jarvis.cache.admin.servlet.AdminServlet</servlet-class>
		<init-param>
			<param-name>cacheManagerConfig</param-name>
			<param-value>org.nutz.integration.autoloadcache.NutzCacheManagerConfig</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>
	<servlet-mapping>
		<servlet-name>cacheadmin</servlet-name>
		<url-pattern>/cacheadmin</url-pattern>
	</servlet-mapping>
```

* 账号：admin
* 密码：admin
