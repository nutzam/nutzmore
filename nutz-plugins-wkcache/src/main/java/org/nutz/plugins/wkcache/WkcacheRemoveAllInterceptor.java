package org.nutz.plugins.wkcache;

import org.nutz.aop.InterceptorChain;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import redis.clients.jedis.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

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
        ScanParams match = new ScanParams().match(cacheName + ":*");
        if (getJedisAgent().isClusterMode()) {
            JedisCluster jedisCluster = getJedisAgent().getJedisClusterWrapper().getJedisCluster();
            List<String> keys = new ArrayList<>();
            for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                try (Jedis jedis = pool.getResource()) {
                    ScanResult<String> scan = null;
                    do {
                        scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                        keys.addAll(scan.getResult());
                    } while (!scan.isCompleteIteration());
                }
            }
            Jedis jedis = null;
            try {
                jedis = getJedisAgent().jedis();
                for (String key : keys) {
                    jedis.del(key);
                }
            } finally {
                Streams.safeClose(jedis);
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = getJedisAgent().jedis();
                ScanResult<String> scan = null;
                do {
                    scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                    for (String key : scan.getResult()) {
                        jedis.del(key.getBytes());
                    }
                } while (!scan.isCompleteIteration());
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
