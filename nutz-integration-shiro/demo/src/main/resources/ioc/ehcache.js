var ioc = {
        cacheManager : {
            type : "net.sf.ehcache.CacheManager",
            factory : "net.sf.ehcache.CacheManager#getCacheManager",
            args : ["shiro-demo"] 
        }
};