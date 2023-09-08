package org.nutz.plugins.wkcache;

import org.nutz.aop.InterceptorChain;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

import java.lang.reflect.Method;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean(singleton = false)
public class WkcacheRemoveAllInterceptor extends AbstractWkcacheInterceptor {
    private String cacheName;
    private boolean isHash;

    public void prepare(CacheDefaults cacheDefaults, CacheRemoveAll cacheRemoveAll, Method method) {
        cacheName = Strings.sNull(cacheRemoveAll.cacheName());
        isHash = cacheDefaults != null && cacheDefaults.isHash();
        if (Strings.isBlank(cacheName)) {
            cacheName = cacheDefaults != null ? cacheDefaults.cacheName() : "wk";
        }
    }

    public void filter(InterceptorChain chain) throws Throwable {
        if (cacheName.contains(",")) {
            for (String name : cacheName.split(",")) {
                if (isHash) {
                    delHashCache(name);
                } else {
                    delCache(name);
                }
            }
        } else {
            if (isHash) {
                delHashCache(cacheName);
            } else {
                delCache(cacheName);
            }
        }
        chain.doChain();
    }

    private void delCache(String cacheName) {
        String lua = "local keysToDelete = redis.call('KEYS', ARGV[1])\n" +
                "for _, key in ipairs(keysToDelete) do\n" +
                "    redis.call('DEL', key)\n" +
                "end";
        if (getJedisAgent().isClusterMode()) {
            JedisCluster jedisCluster = getJedisAgent().getJedisClusterWrapper().getJedisCluster();
            for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.eval(lua, 0, cacheName + ":*");
                } catch (Exception e){
                    //只读节点可能报错,忽略之
                }
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = getJedisAgent().jedis();
                jedis.eval(lua, 0, cacheName + ":*");
            } finally {
                Streams.safeClose(jedis);
            }
        }
    }

    private void delHashCache(String cacheName) {
        Jedis jedis = null;
        try {
            jedis = getJedisAgent().jedis();
            jedis.del(cacheName.getBytes());
        } finally {
            Streams.safeClose(jedis);
        }
    }
}
