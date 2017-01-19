package org.nutz.integration.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * 基于k-v的缓存实现
 * @author wendal
 *
 */
public class RedisCacheManager2 implements CacheManager {

    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return (Cache<K, V>) new RedisCache2<K, V>().setName(name);
    }

}
