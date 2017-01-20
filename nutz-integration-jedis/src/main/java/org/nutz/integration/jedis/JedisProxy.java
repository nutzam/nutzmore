package org.nutz.integration.jedis;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

/**
 * 
 * @author wendal
 *
 */
public class JedisProxy {

    // 通过注入得到
    protected Ioc ioc;
    // 通过注入得到
    protected PropertiesProxy conf;
    // 存储JedisPool,即单机版/主从/Sentinel模式
    protected Pool<Jedis> jedisPool;
    // 将JedisCluster封装为Jedis,就可以实现自动切换了
    protected JedisClusterWrapper jedisClusterWrapper;
    
    /**
     * 若redis.mode=cluster,则返回集群对象,否则返回JedisPool(或Pool<Jedis>)的Jedis实例
     * @return
     */
    public Jedis jedis() {
        if (!"cluster".equals(conf.get("redis.mode")))
            return getJedisPool().getResource();
        return getJedisClusterWrapper();
    }
    
    @SuppressWarnings("unchecked")
    public Pool<Jedis> getJedisPool() {
        if (jedisPool == null)
            jedisPool = ioc.get(Pool.class, "jedisPool");
        return jedisPool;
    }
    
    public JedisClusterWrapper getJedisClusterWrapper() {
        if (jedisClusterWrapper == null)
            jedisClusterWrapper = ioc.get(JedisClusterWrapper.class);
        return jedisClusterWrapper;
    }
}
