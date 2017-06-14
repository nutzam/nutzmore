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
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean
public class WkcacheRemoveAllInterceptor implements MethodInterceptor {
    @Inject("refer:$ioc")
    protected Ioc ioc;

    public void filter(InterceptorChain chain) throws Throwable {
        Method method = chain.getCallingMethod();
        CacheRemoveAll cacheRemoveAll = method.getAnnotation(CacheRemoveAll.class);
        String cacheName = Strings.sNull(cacheRemoveAll.cacheName());
        if (Strings.isBlank(cacheName)) {
            CacheDefaults cacheDefaults = method.getDeclaringClass().getAnnotation(CacheDefaults.class);
            cacheName = cacheDefaults != null ? cacheDefaults.cacheName() : "wk";
        }
        try (Jedis jedis = ioc.get(JedisAgent.class).jedis()) {
            Set<String> set = jedis.keys(cacheName + ":*");
            for (String it : set) {
                jedis.del(it.getBytes());
            }
        }
        chain.doChain();
    }
}
