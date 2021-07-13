package org.nutz.integration.jedis;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.mvc.Mvcs;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.util.Pool;

/**
 * 封装JedisPool和JedisCluster,抹平两者的差异. 通过redis.mode配置, 当等于cluster时,使用JedisCluster, 否则是使用JedisPool
 *
 * @author wendal
 */
public class JedisAgent {

    // 通过注入得到
    protected Ioc ioc;
    // 通过注入得到
    protected PropertiesProxy conf;
    // 存储JedisPool,即单机版/主从/Sentinel模式
    protected Pool<Jedis> jedisPool;
    // 将JedisCluster封装为Jedis,就可以实现自动切换了
    protected JedisClusterWrapper jedisClusterWrapper;

    public JedisAgent() {
    }

    public JedisAgent(Pool<Jedis> jedisPool) {
        this.jedisPool = jedisPool;
        this.conf = new PropertiesProxy();
    }

    public JedisAgent(JedisCluster jedisCluster) {
        super();
        this.jedisClusterWrapper = new JedisClusterWrapper(jedisCluster);
        this.conf = new PropertiesProxy().set("redis.mode", "cluster");
    }


    /**
     * 若redis.mode=cluster,则返回JedisClusterWrapper对象,否则返回JedisPool(或Pool<Jedis>)的Jedis实例
     *
     * @return
     */
    public Jedis jedis() {
        if (jedisPool == null && jedisClusterWrapper == null && ioc == null && conf == null)
            ioc();//触发ioc获取
        if (!"cluster".equals(conf.get("redis.mode")))
            return getJedisPool().getResource();
        return getJedisClusterWrapper();
    }

    public Jedis getResource() {
        return jedis();
    }

    @SuppressWarnings("unchecked")
    public Pool<Jedis> getJedisPool() {
        if (jedisPool == null)
            jedisPool = ioc().get(Pool.class, "jedisPool");
        return jedisPool;
    }

    public JedisClusterWrapper getJedisClusterWrapper() {
        if (jedisClusterWrapper == null)
            jedisClusterWrapper = ioc().get(JedisClusterWrapper.class);
        return jedisClusterWrapper;
    }

    public void setJedisPool(Pool<Jedis> jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void setJedisClusterWrapper(JedisClusterWrapper jedisClusterWrapper) {
        this.jedisClusterWrapper = jedisClusterWrapper;
    }

    public void setConf(PropertiesProxy conf) {
        this.conf = conf;
    }

    public void setIoc(Ioc ioc) {
        this.ioc = ioc;
    }

    public synchronized Ioc ioc() {
        if (ioc == null) {
            ioc = Mvcs.ctx().getDefaultIoc();
        }
        if (ioc != null && conf == null)
            conf = ioc.get(PropertiesProxy.class, "conf");
        return ioc;
    }

    public boolean isReady() {
        try {
            if (jedisPool != null || jedisClusterWrapper != null)
                return true;
            if (ioc() != null)
                return true;
            return Mvcs.ctx().getDefaultIoc() != null;
        } catch (Throwable e) {
            return false;
        }
    }

    public boolean isClusterMode() {
        try {
            if (jedisPool == null && jedisClusterWrapper == null && ioc == null && conf == null)
                ioc();//触发ioc获取
            return "cluster".equals(conf.get("redis.mode"));
        } catch (Throwable e) {
            return false;
        }
    }

    public boolean isSentinelMode() {
        try {
            if (jedisPool == null && jedisClusterWrapper == null && ioc == null && conf == null)
                ioc();//触发ioc获取
            return "sentinel".equals(conf.get("redis.mode"));
        } catch (Throwable e) {
            return false;
        }
    }
}
