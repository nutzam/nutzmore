package org.nutz.plugins.wkcache;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.nutz.aop.InterceptorChain;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemove;

import redis.clients.jedis.Jedis;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean
public class WkcacheRemoveEntryInterceptor extends AbstractWkcacheInterceptor {

    public void filter(InterceptorChain chain) throws Throwable {
        Method method = chain.getCallingMethod();
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        String cacheKey = Strings.sNull(cacheRemove.cacheKey());
        String cacheName = Strings.sNull(cacheRemove.cacheName());
        if (Strings.isBlank(cacheKey)) {
            cacheKey = method.getDeclaringClass().getName()
                       + "."
                       + method.getName()
                       + "#"
                       + Arrays.toString(chain.getArgs());
        }
        if (Strings.isBlank(cacheName)) {
            CacheDefaults cacheDefaults = method.getDeclaringClass()
                                                .getAnnotation(CacheDefaults.class);
            cacheName = cacheDefaults != null ? cacheDefaults.cacheName() : "wk";
        }
        try (Jedis jedis = jedisAgent().getResource()) {
            jedis.del(cacheName + ":" + cacheKey);
        }
        chain.doChain();
    }
}
