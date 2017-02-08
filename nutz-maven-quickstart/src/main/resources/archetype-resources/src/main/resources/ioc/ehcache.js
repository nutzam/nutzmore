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
			factory : "net.sf.ehcache.CacheManager#create"
		}
		 */
};