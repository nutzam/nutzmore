package org.nutz.integration.shiro.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.shiro.cache.CacheException;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.BinaryJedisClusterCommands;
import redis.clients.jedis.BinaryJedisCommands;

@SuppressWarnings("unchecked")
public class RedisCache2<K, V> extends RedisCache<K, V> {

    private static final Log log = Logs.get();

    private String name;

    public RedisCache2<K, V> setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public V get(K key) throws CacheException {
        if (DEBUG)
            log.debugf("GET key=%s:%s", name, key);
        Object jedis = null;
        byte[] buf = null;
        try {
            jedis = LCacheManager.me.jedis();
            if (jedis instanceof BinaryJedisCommands)
                buf = ((BinaryJedisCommands)jedis).get(genKey(key));
            else if (jedis instanceof BinaryJedisClusterCommands)
                buf = ((BinaryJedisClusterCommands)jedis).get(genKey(key));
            return (V) LCacheManager.toObject(buf);
        } finally {
            LCacheManager.safeClose(jedis);
        }
    }

    @Override
    public V put(K key, V value) throws CacheException {
        if (DEBUG)
            log.debugf("SET key=%s:%s", name, key);
        Object jedis = null;
        try {
            jedis = LCacheManager.me.jedis();
            if (jedis instanceof BinaryJedisCommands)
                ((BinaryJedisCommands)jedis).set(genKey(key), LCacheManager.toByteArray(value));
            else if (jedis instanceof BinaryJedisClusterCommands)
                ((BinaryJedisClusterCommands)jedis).set(genKey(key), LCacheManager.toByteArray(value));
            return null;
        } finally {
            LCacheManager.safeClose(jedis);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        if (DEBUG)
            log.debugf("DEL key=%s:%s", name, key);
        Object jedis = null;
        try {
            jedis = LCacheManager.me.jedis();
            if (jedis instanceof BinaryJedisCommands)
                ((BinaryJedisCommands)jedis).del(genKey(key));
            else if (jedis instanceof BinaryJedisClusterCommands)
                ((BinaryJedisClusterCommands)jedis).del(genKey(key));
            return null;
        } finally {
            LCacheManager.safeClose(jedis);
        }
    }

    public void clear() throws CacheException {
        if (DEBUG)
            log.debugf("CLR name=%s", name);
    }

    public int size() {
        if (DEBUG)
            log.debugf("SIZ name=%s", name);
        return 0;
    }

    public Set<K> keys() {
        if (DEBUG)
            log.debugf("KEYS name=%s", name);
        return Collections.EMPTY_SET;
    }

    public Collection<V> values() {
        if (DEBUG)
            log.debugf("VLES name=%s", name);
        return Collections.EMPTY_LIST;
    }
    
    protected byte[] genKey(Object key) {
        return (name + ":" + key).getBytes();
    }

}
