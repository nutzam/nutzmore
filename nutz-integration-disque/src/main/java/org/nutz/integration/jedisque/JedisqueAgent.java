package org.nutz.integration.jedisque;

import com.github.xetorthio.jedisque.Jedisque;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.mvc.Mvcs;

import redis.clients.jedis.JedisCluster;
import redis.clients.util.Pool;

/**
 * 封装JedisPool和JedisCluster,抹平两者的差异. 通过redis.mode配置, 当等于cluster时,使用JedisCluster, 否则是使用JedisPool
 * @author wendal
 *
 */
public class JedisqueAgent {

    // 通过注入得到
    protected Ioc ioc;
    // 通过注入得到
    protected PropertiesProxy conf;
    // 存储JedisPool,即单机版/主从/Sentinel模式
    protected Pool<Jedisque> jedisquePool;
    // 将JedisCluster封装为Jedis,就可以实现自动切换了
    protected JedisqueClusterWrapper jedisClusterWrapper;
    
    public JedisqueAgent() {}

    public JedisqueAgent(Pool<Jedisque> jedisquePool) {
        this.jedisquePool = jedisquePool;
        this.conf = new PropertiesProxy();
    }

    public JedisqueAgent(JedisqueCluster jedisCluster) {
        super();
        this.jedisClusterWrapper = new JedisqueClusterWrapper(jedisCluster);
        this.conf = new PropertiesProxy().set("redis.mode", "cluster");
    }


    /**
     * 若redis.mode=cluster,则返回JedisClusterWrapper对象,否则返回JedisPool(或Pool<Jedis>)的Jedis实例
     * @return
     */
    public Jedisque jedisque() {
        if (jedisquePool == null && jedisClusterWrapper == null && ioc == null && conf == null)
            ioc();//触发ioc获取
        if (!"cluster".equals(conf.get("redis.mode")))
            return getJedisquePool().getResource();
        return getJedisqueClusterWrapper();
    }
    
    public Jedisque getResource() {
        return jedisque();
    }
    
    @SuppressWarnings("unchecked")
    public Pool<Jedisque> getJedisPool() {
        if (jedisquePool == null)
            jedisquePool = ioc().get(Pool.class, "jedisquePool");
        return jedisquePool;
    }
    
    public JedisqueClusterWrapper getJedisClusterWrapper() {
        if (jedisClusterWrapper == null)
            jedisClusterWrapper = ioc().get(JedisqueClusterWrapper.class);
        return jedisClusterWrapper;
    }
    
    public void setJedisquePool(Pool<Jedisque> jedisPool) {
        this.jedisquePool = jedisquePool;
    }
    
    public void setJedisqueClusterWrapper(JedisqueClusterWrapper jedisqueClusterWrapper) {
        this.jedisqueClusterWrapper = jedisqueClusterWrapper;
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
            if (jedisquePool != null || jedisqueClusterWrapper != null)
                return true;
            if (ioc() != null)
                return true;
            return Mvcs.ctx().getDefaultIoc() != null;
        }
        catch (Throwable e) {
            return false;
        }
    }
}
