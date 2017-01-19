package org.nutz.integration.shiro.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.shiro.ShiroException;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;
import org.apache.shiro.util.Initializable;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.util.Pool;

@SuppressWarnings({"unchecked", "rawtypes"})
public class LCacheManager implements CacheManager, Initializable, Destroyable, Runnable {
    
    private static final Log log = Logs.get();

    public static String PREFIX = "LCache:";
    
    protected String id = R.UU32();

    protected CacheManager level1;
    protected CacheManager level2;

    protected Pool<Jedis> pool;
    protected JedisCluster jedisCluster;
    protected CachePubSub pubSub = new CachePubSub();
    protected Map<String, LCache> caches = new HashMap<String, LCache>();

    protected static LCacheManager me;

    public static LCacheManager me() {
        return me;
    }

    public LCacheManager() {
        me = this;
    }
    
    public Object jedis() {
        if (pool != null)
            return pool.getResource();
        return jedisCluster;
    }

    public void setupJedisPool(Pool<Jedis> pool) {
        this.pool = pool;
        new Thread(this, "lcache.pubsub").start();
    }
    
    public void setupJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
        new Thread(this, "lcache.pubsub").start();
    }
    
    public void run() {
        int count = 1;
        if (pool != null) {
            while (!pool.isClosed()) {
                try {
                    log.debug("psubscribe " + PREFIX + "*");
                    pool.getResource().psubscribe(pubSub, PREFIX + "*");
                }
                catch (Exception e) {
                    if (pool.isClosed())
                        break;
                    log.debug("psubscribe fail, retry after "+count+"seconds", e);
                    Lang.quiteSleep(count * 1000);
                    if (count < 15)
                        count ++;
                }
            }
        }
        else if (jedisCluster != null) {
            while (isClusterRunning()) {
                try {
                    log.debug("psubscribe " + PREFIX + "*");
                    jedisCluster.psubscribe(pubSub, PREFIX + "*");
                }
                catch (Exception e) {
                    if (!isClusterRunning())
                        break;
                    log.debug("psubscribe fail, retry after "+count+"seconds", e);
                    Lang.quiteSleep(count * 1000);
                    if (count < 15)
                        count ++;
                }
            }
        }
    }
    
    protected boolean isClusterRunning() {
        boolean running = false;
        for (Entry<String, JedisPool> en : jedisCluster.getClusterNodes().entrySet()) {
            running |= !en.getValue().isClosed();
        }
        return running;
    }

    public void depose() {
        pubSub.punsubscribe(PREFIX + "*");
    }

    @Override
    public void destroy() throws Exception {
        if (level2 != null && level2 instanceof Destroyable)
            ((Destroyable) level2).destroy();
        if (level1 != null && level1 instanceof Destroyable)
            ((Destroyable) level1).destroy();
    }

    @Override
    public void init() throws ShiroException {
        if (level2 != null && level2 instanceof Initializable)
            ((Initializable) level2).init();
        if (level1 != null && level1 instanceof Initializable)
            ((Initializable) level1).init();
    }

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        LCache<K, V> combo = caches.get(name);
        if (combo != null)
            return combo;
        combo = new LCache<K, V>(name);
        if (level1 != null)
            combo.add((Cache<K, V>) level1.getCache(name));
        if (level2 != null)
            combo.add((Cache<K, V>) level2.getCache(name));
        caches.put(name, combo);
        return combo;
    }

    public void setLevel1(CacheManager level1) {
        this.level1 = level1;
    }

    public void setLevel2(CacheManager level2) {
        this.level2 = level2;
    }

    public static void safeClose(Object obj) {
        if (obj == null)
            return;
        if (obj instanceof Jedis)
            Streams.safeClose((Closeable)obj);
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
        if (buf == null)
            return null;
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
