package org.nutz.plugins.cache.impl.redis;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.nutz.plugins.cache.CacheSerializer;
import org.nutz.plugins.cache.serializer.DefaultJdkSerializer;

/**
 * 基于 jedis的缓存实现
 * @author wendal
 *
 */
public class RedisCacheManager implements CacheManager {
    
    /**
     * 模式设置.<p/>
     * kv模式使用get/set组合,支持ttl,内存占用较多. <p/>
     * hset模式使用hget/hset组合,不支持ttl, 内存占用较少. <p/>
     */
    protected String mode;
    
    /**
     * 是否输出详细的日志
     */
    protected boolean debug;
    
    /**
     * 默认过期时间,仅mode=kv时生效
     */
    protected int ttl;
    
    /**
     * 序列化器,一般不需要管
     */
    protected CacheSerializer serializer = new DefaultJdkSerializer();

    public <K, V> Cache<K, V> getCache(String name) {
        RedisCache<K, V> cache = null;
        if (mode == null || !mode.equals("kv"))
            cache = new RedisCache<K, V>();
        else
            cache = new RedisCache2<K, V>();
        cache.setName(name).setDebug(debug).setSerializer(serializer).setTtl(ttl);
        return (Cache<K, V>) cache;
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }

    public void init() {}

    public void depose() {}

    public void setDebug(boolean debug) {
        this.debug = debug;
    }
    
    public void setSerializer(CacheSerializer serializer) {
        this.serializer = serializer;
    }
    
    public void setTtl(int ttl) {
        this.ttl = ttl;
    }
}
