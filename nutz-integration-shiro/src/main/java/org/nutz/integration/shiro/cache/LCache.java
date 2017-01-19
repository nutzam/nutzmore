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

import redis.clients.jedis.MultiKeyCommands;
import redis.clients.jedis.MultiKeyJedisClusterCommands;

public class LCache<K, V> implements Cache<K, V> {
    
    private static final Log log = Logs.get();

    protected List<Cache<K, V>> list = new ArrayList<Cache<K, V>>();

    protected String name;

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
        Object jedis = null;
        try {
            jedis = LCacheManager.me.jedis();
            String channel = (LCacheManager.PREFIX + name);
            String msg = LCacheManager.me().id + ":" + key;
            log.debugf("fire channel=%s msg=%s", channel, msg);
            if (jedis instanceof MultiKeyCommands)
                ((MultiKeyCommands)jedis).publish(channel, msg);
            else if (jedis instanceof MultiKeyJedisClusterCommands)
                ((MultiKeyJedisClusterCommands)jedis).publish(channel, msg);
        } finally {
            LCacheManager.safeClose(jedis);
        }
    }
}
