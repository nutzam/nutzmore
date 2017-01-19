package org.nutz.integration.shiro.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@SuppressWarnings("unchecked")
public class RedisCache<K, V> implements Cache<K, V> {

    private static final Log log = Logs.get();

    public static boolean DEBUG = false;

    private String name;
    private byte[] nameByteArray;

    protected JedisPool _pool() {
        return LCacheManager.me.jedisPool;
    }

    public RedisCache<K, V> setName(String name) {
        this.name = name;
        this.nameByteArray = name.getBytes();
        return this;
    }

    @Override
    public V get(K key) throws CacheException {
        if (DEBUG)
            log.debugf("GET name=%s key=%s", name, key);
        Jedis jedis = null;
        try {
            jedis = _pool().getResource();
            byte[] buf = jedis.hget(nameByteArray, genKey(key));
            if (buf == null)
                return null;
            return (V) toObject(buf);
        } finally {
            Streams.safeClose(jedis);
        }
    }

    @Override
    public V put(K key, V value) throws CacheException {
        if (DEBUG)
            log.debugf("SET name=%s key=%s", name, key);
        Jedis jedis = null;
        try {
            jedis = _pool().getResource();
            jedis.hset(nameByteArray, genKey(key), toByteArray(value));
            return null;
        } finally {
            Streams.safeClose(jedis);
        }
    }

    @Override
    public V remove(K key) throws CacheException {
        if (DEBUG)
            log.debugf("DEL name=%s key=%s", name, key);
        // TODO 应使用pipeline
        // V prev = get(key);
        Jedis jedis = null;
        try {
            jedis = _pool().getResource();
            jedis.hdel(nameByteArray, genKey(key));
            return null;
        } finally {
            Streams.safeClose(jedis);
        }
    }

    @Override
    public void clear() throws CacheException {
        if (DEBUG)
            log.debugf("CLR name=%s", name);
        Jedis jedis = null;
        try {
            jedis = _pool().getResource();
            jedis.del(nameByteArray);
        } finally {
            Streams.safeClose(jedis);
        }
    }

    public int size() {
        if (DEBUG)
            log.debugf("SIZ name=%s", name);
        Jedis jedis = null;
        try {
            jedis = _pool().getResource();
            return jedis.hlen(nameByteArray).intValue();
        } finally {
            Streams.safeClose(jedis);
        }
    }

    public Set<K> keys() {
        if (DEBUG)
            log.debugf("KEYS name=%s", name);
        Jedis jedis = null;
        try {
            jedis = _pool().getResource();
            return (Set<K>) jedis.hkeys(name);
        } finally {
            Streams.safeClose(jedis);
        }
    }

    @Override
    public Collection<V> values() {
        if (DEBUG)
            log.debugf("VLES name=%s", name);
        Jedis jedis = null;
        try {
            jedis = _pool().getResource();
            List<byte[]> vals = jedis.hvals(nameByteArray);
            List<V> list = new ArrayList<V>();
            for (byte[] buf : vals) {
                list.add((V) toObject(buf));
            }
            return list;
        } finally {
            Streams.safeClose(jedis);
        }
    }

    protected byte[] genKey(Object key) {
        return key.toString().getBytes();
    }

    public static final byte[] toByteArray(Object obj) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(out);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            return out.toByteArray();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final Object toObject(byte[] buf) {
        try {
            ByteArrayInputStream ins = new ByteArrayInputStream(buf);
            ObjectInputStream ois = new ObjectInputStream(ins);
            return ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
