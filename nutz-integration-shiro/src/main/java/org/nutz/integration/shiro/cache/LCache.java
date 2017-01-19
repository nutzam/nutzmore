package org.nutz.integration.shiro.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class LCache<K, V> implements Cache<K, V> {
    
    private static final Log log = Logs.get();

    protected List<Cache<K, V>> list = new ArrayList<Cache<K, V>>();

    protected String name;

    protected JedisPool pool;

    public LCache(String name) {
        this.name = name;
    }

    public void add(Cache<K, V> cache) {
        list.add(cache);
    }

    public V get(K key) throws CacheException {
        for (Cache<K, V> cache : list) {
            V v = cache.get(key);
            if (v != null)
                return v;
        }
        return null;
    }

    @Override
    public V put(K key, V value) throws CacheException {
        for (Cache<K, V> cache : list)
            cache.put(key, value);
        fire(genKey(key));
        return null;
    }

    public V remove(K key) throws CacheException {
        for (Cache<K, V> cache : list)
            cache.remove(key);
        fire(genKey(key));
        return null;
    }

    @Override
    public void clear() throws CacheException {
        for (Cache<K, V> cache : list) {
            cache.clear();
        }
    }

    @Override
    public int size() {
        return keys().size();
    }

    @Override
    public Set<K> keys() {
        Set<K> keys = new HashSet<K>();
        for (Cache<K, V> cache : list) {
            keys.addAll(cache.keys());
        }
        return keys;
    }

    @Override
    public Collection<V> values() {
        Set<V> values = new HashSet<V>();
        for (Cache<K, V> cache : list) {
            values.addAll(cache.values());
        }
        return values;
    }

    public String genKey(K k) {
        return k.toString();
    }

    public void fire(String key) {
        if (pool == null)
            pool = LCacheManager.me.jedisPool;
        if (pool != null) {
            Jedis jedis = null;
            try {
                jedis = pool.getResource();
                String channel = (LCacheManager.PREFIX + name);
                String msg = LCacheManager.me().id + ":" + key;
                log.debugf("fire channel=%s msg=%s", channel, msg);
                jedis.publish(channel, msg);
            }
            catch (Exception e) {}
            finally {
                if (jedis != null)
                    jedis.close();
            }
        }
    }
}
