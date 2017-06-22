package org.nutz.plugins.wkcache;

import java.lang.reflect.Method;
import java.util.Set;

import org.nutz.aop.InterceptorChain;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;

import redis.clients.jedis.Jedis;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean
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
        try (Jedis jedis = jedisAgent().getResource()) {
            Set<String> set = jedis.keys(cacheName + ":*");
            for (String it : set) {
                jedis.del(it.getBytes());
            }
        }
        chain.doChain();
    }
}
