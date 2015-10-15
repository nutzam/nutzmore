package org.nutz.plugins.cache.dao.impl.provider;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.CacheResult;
import org.nutz.plugins.cache.dao.CachedNutDaoExecutor;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisDaoCacheProvider extends AbstractDaoCacheProvider {

    private static final Log log = Logs.get();
    
    protected JedisPool jedisPool;
    
    protected String script;

    public Object get(String cacheName, String key) {
        byte[] obj = null;
        try (Jedis jedis = jedisPool.getResource()) {
            obj = jedis.hget(cacheName.getBytes(), key.getBytes());
        } finally{}
        if (obj != null) {
            return getSerializer().back(obj);
        }
        return CacheResult.NOT_FOUNT;
    }

    public boolean put(String cacheName, String key, Object obj) {
        Object data = getSerializer().from(obj);
        if (data == null) {
            if (CachedNutDaoExecutor.DEBUG)
                log.debug("Serializer.from >> NULL");
            return false;
        }
        if (CachedNutDaoExecutor.DEBUG)
            log.debugf("CacheName=%s, KEY=%s", cacheName, key);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.hset(cacheName.getBytes(), key.getBytes(), (byte[])data);
        } finally{}
        return true;
    }

    public void clear(String cacheName) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(cacheName.getBytes());
        } finally{}
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
    
    public void init() throws Throwable {
        super.init();
    }
}
