package org.nutz.plugins.cache.impl.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.CacheSerializer;
import org.nutz.plugins.cache.impl.lcache.LCacheManager;
import org.nutz.plugins.cache.serializer.DefaultJdkSerializer;

import redis.clients.jedis.Jedis;

@SuppressWarnings("unchecked")
public class RedisCache<K, V> implements Cache<K, V> {

    private static final Log log = Logs.get();

    public static boolean DEBUG = false;

    private String name;
    private byte[] nameByteArray;
    
    protected CacheSerializer serializer = new DefaultJdkSerializer();

    public RedisCache<K, V> setName(String name) {
        this.name = name;
        this.nameByteArray = name.getBytes();
        return this;
    }

    @Override
    public V get(K key) {
        if (DEBUG)
            log.debugf("HGET name=%s key=%s", name, key);
        Jedis jedis = null;
        byte[] buf = null;
        try {
            jedis = jedis();
            buf = jedis.hget(nameByteArray, genKey(key));
            return (V) serializer.toObject(buf);
        } finally {
            Streams.safeClose(jedis);
        }
    }

    @Override
    public V put(K key, V value) {
        if (DEBUG)
            log.debugf("HSET name=%s key=%s", name, key);
        Jedis jedis = null;
        try {
            jedis = jedis();
            jedis.hset(nameByteArray, genKey(key), (byte[])serializer.fromObject(value));
            return null;
        } finally {
            Streams.safeClose(jedis);
        }
    }

    @Override
    public V remove(K key) {
        if (DEBUG)
            log.debugf("HDEL name=%s key=%s", name, key);
        Jedis jedis = null;
        try {
            jedis = jedis();
            jedis.hdel(nameByteArray, genKey(key));
            return null;
        } finally {
            Streams.safeClose(jedis);
        }
    }

    @Override
    public void clear() {
        if (DEBUG)
            log.debugf("DEL name=%s", name);
        Jedis jedis = null;
        try {
            jedis = jedis();
            jedis.del(nameByteArray);
        } finally {
            Streams.safeClose(jedis);
        }
    }

    public int size() {
        if (DEBUG)
            log.debugf("HLEN name=%s", name);
        Jedis jedis = null;
        try {
            jedis = jedis();
            return jedis.hlen(nameByteArray).intValue();
        } finally {
            Streams.safeClose(jedis);
        }
    }

    public Set<K> keys() {
        if (DEBUG)
            log.debugf("HKEYS name=%s", name);
        Jedis jedis = null;
        try {
            jedis = jedis();
            return (Set<K>) jedis.hkeys(name);
        } finally {
            Streams.safeClose(jedis);
        }
    }

    @Override
    public Collection<V> values() {
        if (DEBUG)
            log.debugf("HVALES name=%s", name);
        Jedis jedis = null;
        try {
            jedis = jedis();
            Collection<byte[]> vals = jedis.hvals(nameByteArray);
            List<V> list = new ArrayList<V>();
            for (byte[] buf : vals)
                list.add((V) serializer.fromObject(buf));
            return list;
        } finally {
            Streams.safeClose(jedis);
        }
    }
    
    protected byte[] genKey(Object key) {
        return key.toString().getBytes();
    }
    
    protected Jedis jedis() {
        return LCacheManager.me().jedis();
    }
}
