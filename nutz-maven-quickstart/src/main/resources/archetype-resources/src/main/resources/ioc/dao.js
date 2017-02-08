var ioc = {
		conf : {
			type : "org.nutz.ioc.impl.PropertiesProxy",
			fields : {
				ignoreResourceNotFound : true,
				paths : ["custom/", {env:["JAVA_HOME", "/conf/"]}]
			}
		},
	    dataSource : {
	        factory : "$conf#make",
	        args : ["com.alibaba.druid.pool.DruidDataSource", "db."],
	        type : "com.alibaba.druid.pool.DruidDataSource",
	        events : {
	        	create : "init",
	            depose : 'close'
	        }
	    },
	    slaveDataSource : {
	        factory : "$conf#make",
	        args : ["com.alibaba.druid.pool.DruidDataSource", "db."],
	        type : "com.alibaba.druid.pool.DruidDataSource",
	        events : {
	        	create : "init",
	            depose : 'close'
	        }
	    },
		dao : {
			type : "org.nutz.dao.impl.NutDao",
			args : [{refer:"dataSource"}],
			fields : {
				//executor : {refer: "cacheExecutor"},
				runner : {refer: "daoRunner"},
				interceptors : [{refer:"cacheExecutor"}, "log", "time"]
			}
		},
		daoRunner : {
			type : "org.nutz.dao.impl.sql.run.NutDaoRunner",
			fields : {
				slaveDataSource : {refer:"slaveDataSource"}
			}
		},
		cacheExecutor : {
			type : "org.nutz.plugins.cache.dao.DaoCacheInterceptor",
			//type : "org.nutz.plugins.cache.dao.CachedNutDaoExecutor",
			//type : "net.wendal.nutzbook.common.util.MasterSlaveDaoExecutor",
			fields : {
				cacheProvider : {refer:"cacheProvider"},
				cachedTableNames : [ 
				    "t_user_profile", "t_user", "t_role",
					"t_permission", "t_role_permission",  "t_permission_category",
					"t_topic_reply", "t_big_content",
					"t_oauth_user", "t_user_role",
					"t_sub_forum"],
				
				//slave : {refer:"dataSource_slave"}
		}
	},
	fst : {
		type : "net.wendal.nutzbook.common.util.FstCacheSerializer"
	},
	/*
	// 基于内存的简单LRU实现
	cacheProvider : {
		type : "org.nutz.plugins.cache.dao.impl.provider.MemoryDaoCacheProvider",
		fields : {
			cacheSize : 10000 // 缓存的对象数
		},
		events : {
			create : "init"
		}
	}
	 */
	// 基于Ehcache的DaoCacheProvider
	cacheProvider : {
		type : "org.nutz.plugins.cache.dao.impl.provider.EhcacheDaoCacheProvider",
		fields : {
			cacheManager : {
				refer : "cacheManager"
			},
			//serializer : {refer:"fst"}
		// 引用ehcache.js中定义的CacheManager
		},
		events : {
			create : "init"
		}
	}
};