package org.nutz.plugins.wkcache;

import org.nutz.aop.InterceptorChain;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;

import java.lang.reflect.Method;
import java.util.Set;

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
        Set<byte[]> set = redisService().keys((cacheName + ":*").getBytes());
        for (byte[] it : set) {
            redisService().del(it);
        }
        chain.doChain();
    }
}
