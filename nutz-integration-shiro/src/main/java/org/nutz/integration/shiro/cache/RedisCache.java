package org.nutz.integration.shiro.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.BinaryJedisClusterCommands;
import redis.clients.jedis.BinaryJedisCommands;

@SuppressWarnings("unchecked")
public class RedisCache<K, V> implements Cache<K, V> {

    private static final Log log = Logs.get();

    public static boolean DEBUG = false;

    private String name;
    private byte[] nameByteArray;

    public RedisCache<K, V> setName(String name) {
        this.name = name;
        this.nameByteArray = name.getBytes();
        return this;
    }

    @Override
    public V get(K key) throws CacheException {
        if (DEBUG)
            log.debugf("HGET name=%s key=%s", name, key);
        Object jedis = null;
        byte[] buf = null;
        try {
            jedis = LCacheManager.me.jedis();
            if (jedis instanceof BinaryJedisCommands)
                buf = ((BinaryJedisCommands)jedis).hget(nameByteArray, genKey(key));
            else if (jedis instanceof BinaryJedisClusterCommands)
                buf = ((BinaryJedisClusterCommands)jedis).hget(nameByteArray, genKey(key));
            return (V) LCacheManager.toObject(buf);
        } finally {
            LCacheManager.safeClose(jedis);
        }
    }

    @Override
    public V put(K key, V value) throws CacheException {
        if (DEBUG)
            log.debugf("HSET name=%s key=%s", name, key);
        Object jedis = null;
        try {
            jedis = LCacheManager.me.jedis();
            if (jedis instanceof BinaryJedisCommands)
                ((BinaryJedisCommands)jedis).hset(nameByteArray, genKey(key), LCacheManager.toByteArray(value));
            else if (jedis instanceof BinaryJedisClusterCommands)
                ((BinaryJedisClusterCommands)jedis).hset(nameByteArray, genKey(key), LCacheManager.toByteArray(value));
            return null;
        } finally {
            LCacheManager.safeClose(jedis);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        if (DEBUG)
            log.debugf("HDEL name=%s key=%s", name, key);
        Object jedis = null;
        try {
            jedis = LCacheManager.me.jedis();
            if (jedis instanceof BinaryJedisCommands)
                ((BinaryJedisCommands)jedis).hdel(nameByteArray, genKey(key));
            else if (jedis instanceof BinaryJedisClusterCommands)
                ((BinaryJedisClusterCommands)jedis).hdel(nameByteArray, genKey(key));
            return null;
        } finally {
            LCacheManager.safeClose(jedis);
        }
    }

    @Override
    public void clear() throws CacheException {
        if (DEBUG)
            log.debugf("DEL name=%s", name);
        Object jedis = null;
        try {
            jedis = LCacheManager.me.jedis();
            if (jedis instanceof BinaryJedisCommands)
                ((BinaryJedisCommands)jedis).del(nameByteArray);
            else if (jedis instanceof BinaryJedisClusterCommands)
                ((BinaryJedisClusterCommands)jedis).del(nameByteArray);
        } finally {
            LCacheManager.safeClose(jedis);
        }
    }

    public int size() {
        if (DEBUG)
            log.debugf("HLEN name=%s", name);
        Object jedis = null;
        try {
            jedis = LCacheManager.me.jedis();
            if (jedis instanceof BinaryJedisCommands)
                return ((BinaryJedisCommands)jedis).hlen(nameByteArray).intValue();
            else if (jedis instanceof BinaryJedisClusterCommands)
                return ((BinaryJedisClusterCommands)jedis).hlen(nameByteArray).intValue();
            return 0;
        } finally {
            LCacheManager.safeClose(jedis);
        }
    }

    public Set<K> keys() {
        if (DEBUG)
            log.debugf("HKEYS name=%s", name);
        Object jedis = null;
        try {
            jedis = LCacheManager.me.jedis();
            Set<byte[]> keys = null;
            if (jedis instanceof BinaryJedisCommands)
                keys = ((BinaryJedisCommands)jedis).hkeys(nameByteArray);
            else if (jedis instanceof BinaryJedisClusterCommands)
                keys = ((BinaryJedisClusterCommands)jedis).hkeys(nameByteArray);
            if (keys == null || keys.size() == 0)
                return new HashSet<K>();
            HashSet<String> set = new HashSet<String>();
            for (byte[] bs : keys) {
                set.add(new String(bs));
            }
            return (Set<K>) set;
        } finally {
            LCacheManager.safeClose(jedis);
        }
    }

    @Override
    public Collection<V> values() {
        if (DEBUG)
            log.debugf("HVALES name=%s", name);
        Object jedis = null;
        try {
            jedis = LCacheManager.me.jedis();
            Collection<byte[]> vals = null;
            if (jedis instanceof BinaryJedisCommands)
                vals = ((BinaryJedisCommands)jedis).hvals(nameByteArray);
            else if (jedis instanceof BinaryJedisClusterCommands)
                vals = ((BinaryJedisClusterCommands)jedis).hvals(nameByteArray);
            if (vals == null)
                return Collections.EMPTY_LIST;
            List<V> list = new ArrayList<V>();
            for (byte[] buf : vals)
                list.add((V) LCacheManager.toObject(buf));
            return list;
        } finally {
            LCacheManager.safeClose(jedis);
        }
    }
    
    protected byte[] genKey(Object key) {
        return key.toString().getBytes();
    }
}
