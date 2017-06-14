package org.nutz.plugins.wkcache;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemove;
import org.nutz.plugins.wkcache.annotation.CacheResult;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean
public class WkcacheRemoveEntryInterceptor implements MethodInterceptor {
    @Inject("refer:$ioc")
    protected Ioc ioc;

    public void filter(InterceptorChain chain) throws Throwable {
        Method method = chain.getCallingMethod();
        CacheRemove cacheRemove = method.getAnnotation(CacheRemove.class);
        String cacheKey = Strings.sNull(cacheRemove.cacheKey());
        String cacheName = Strings.sNull(cacheRemove.cacheName());
        if (Strings.isBlank(cacheKey)) {
            cacheKey = method.getDeclaringClass().getName() + "." + method.getName() + "#" + Arrays.toString(chain.getArgs());
        }
        if (Strings.isBlank(cacheName)) {
            CacheDefaults cacheDefaults = method.getDeclaringClass().getAnnotation(CacheDefaults.class);
            cacheName = cacheDefaults != null ? cacheDefaults.cacheName() : "wk";
        }
        try (Jedis jedis = ioc.get(JedisAgent.class).jedis()) {
            jedis.del(cacheName + ":" + cacheKey);
        }
        chain.doChain();
    }
}
