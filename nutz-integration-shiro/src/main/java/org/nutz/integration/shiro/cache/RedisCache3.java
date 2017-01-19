package org.nutz.integration.shiro.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.JedisCluster;

@SuppressWarnings("unchecked")
public class RedisCache3<K, V> implements Cache<K, V> {

    private static final Log log = Logs.get();
    
    private String name;
    private byte[] nameByteArray;

    protected JedisCluster jedisc() {
        return LCacheManager.me.jedisCluster;
    }

    public RedisCache3<K, V> setName(String name) {
        this.name = name;
        this.nameByteArray = (name+":").getBytes();
        return this;
    }

    @Override
    public V get(K key) throws CacheException {
        if (RedisCache.DEBUG)
            log.debugf("GET name=%s key=%s", name, key);
        byte[] buf = jedisc().get(genKey(key));
        if (buf == null)
            return null;
        return (V) toObject(buf);
    }

    @Override
    public V put(K key, V value) throws CacheException {
        if (RedisCache.DEBUG)
            log.debugf("SET name=%s key=%s", name, key);
        jedisc().set(genKey(key), toByteArray(value));
        return null;
    }

    @Override
    public V remove(K key) throws CacheException {
        if (RedisCache.DEBUG)
            log.debugf("DEL name=%s key=%s", name, key);
        jedisc().del(genKey(key));
        return null;
    }

    @Override
    public void clear() throws CacheException {
        if (RedisCache.DEBUG)
            log.debugf("CLR name=%s", name);
    }

    public int size() {
        if (RedisCache.DEBUG)
            log.debugf("SIZE name=%s", name);
        return 0;
    }

    public Set<K> keys() {
        if (RedisCache.DEBUG)
            log.debugf("KEYS name=%s", name);
        return Collections.EMPTY_SET;
    }

    @Override
    public Collection<V> values() {
        if (RedisCache.DEBUG)
            log.debugf("VLES name=%s", name);
        return Collections.EMPTY_LIST;
    }

    protected byte[] genKey(Object key) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(nameByteArray);
            out.write(key.toString().getBytes());
            return out.toByteArray();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
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
