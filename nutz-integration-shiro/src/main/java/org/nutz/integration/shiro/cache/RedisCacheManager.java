package org.nutz.integration.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * 基于 hset的缓存实现
 * @author wendal
 *
 */
public class RedisCacheManager implements CacheManager {

    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return (Cache<K, V>) new RedisCache<K, V>().setName(name);
    }

}
