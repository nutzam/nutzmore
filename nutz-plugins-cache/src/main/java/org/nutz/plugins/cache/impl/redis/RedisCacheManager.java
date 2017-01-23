package org.nutz.plugins.cache.impl.redis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;

/**
 * 基于 hset的缓存实现
 * @author wendal
 *
 */
public class RedisCacheManager implements CacheManager {
    
    protected String mode;
    
    protected boolean debug;

    public <K, V> Cache<K, V> getCache(String name) {
        if (mode == null || !mode.equals("kv"))
            return (Cache<K, V>) new RedisCache<K, V>().setName(name).setDebug(debug);;
        return (Cache<K, V>) new RedisCache2<K, V>().setName(name).setDebug(debug);
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }

    public void init() {}

    public void depose() {}

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
}
