package org.nutz.plugins.wkcache;

import org.nutz.aop.InterceptorChain;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import redis.clients.jedis.*;

import java.lang.reflect.Method;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean(singleton = false)
public class WkcacheRemoveAllInterceptor extends AbstractWkcacheInterceptor {

    public void filter(InterceptorChain chain) throws Throwable {
        Method method = chain.getCallingMethod();
        CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);
        String cacheName = Strings.sNull(cacheRemoveAll.cacheName());
        if (Strings.isBlank(cacheName)) {
            CacheDefaults cacheDefaults = method.getDeclaringClass()
                    .getAnnotation(CacheDefaults.class);
            cacheName = cacheDefaults != null ? cacheDefaults.cacheName() : "wk";
        }
        if (cacheName.contains(",")) {
            for (String name : cacheName.split(",")) {
                delCache(name);
            }
        } else {
            delCache(cacheName);
        }
        chain.doChain();
    }

    private void delCache(String cacheName) {
        if (getJedisAgent().isClusterMode()) {
            JedisCluster jedisCluster = getJedisAgent().getJedisClusterWrapper().getJedisCluster();
            for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                try (Jedis jedis = pool.getResource()) {
                    ScanParams match = new ScanParams().match(cacheName + ":*");
                    ScanResult<String> scan = null;
                    do {
                        scan = jedis.scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                        for (String key : scan.getResult()) {
                            jedis.del(key.getBytes());
                        }
                    } while (!scan.isCompleteIteration());
                }
            }
        } else {
            // 使用 scan 指令来查找所有匹配到的 Key
            ScanParams match = new ScanParams().match(cacheName + ":*");
            ScanResult<String> scan = null;
            do {
                scan = getJedisAgent().jedis().scan(scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                for (String key : scan.getResult()) {
                    getJedisAgent().jedis().del(key.getBytes());
                }
                // 已经迭代结束了
            } while (!scan.isCompleteIteration());
        }
    }
}
