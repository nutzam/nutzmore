package org.nutz.integration.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;

/**
 * 基于JedisCluster的集合方案
 * @author wendal
 *
 */
public class RedisCacheManager3 implements CacheManager {

    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return (Cache<K, V>) new RedisCache3<K, V>().setName(name);
    }

}
