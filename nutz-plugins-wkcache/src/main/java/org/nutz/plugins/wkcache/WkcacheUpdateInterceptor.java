package org.nutz.plugins.wkcache;

import org.nutz.aop.InterceptorChain;
import org.nutz.el.El;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheUpdate;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean
public class WkcacheUpdateInterceptor extends AbstractWkcacheInterceptor {

    public void filter(InterceptorChain chain) throws Throwable {
        Method method = chain.getCallingMethod();
        CacheUpdate cacheResult = method.getAnnotation(CacheUpdate.class);
        String cacheKey = Strings.sNull(cacheResult.cacheKey());
        String cacheName = Strings.sNull(cacheResult.cacheName());
        int liveTime = 0;
        if (Strings.isBlank(cacheKey)) {
            cacheKey = method.getDeclaringClass().getName()
                    + "."
                    + method.getName()
                    + "#"
                    + Arrays.toString(chain.getArgs());
        } else {
            this.key = new CharSegment(cacheKey);
            if (key.hasKey()) {
                Context ctx = Lang.context();
                ctx.set("args", chain.getArgs());
                Context _ctx = Lang.context();
                for (String key : key.keys()) {
                    _ctx.set(key, new El(key).eval(ctx));
                }
                cacheKey = key.render(_ctx).toString();
            } else {
                cacheKey = key.getOrginalString();
            }
        }
        if (Strings.isBlank(cacheName)) {
            CacheDefaults cacheDefaults = method.getDeclaringClass()
                    .getAnnotation(CacheDefaults.class);
            cacheName = cacheDefaults != null ? cacheDefaults.cacheName() : "wk";
            liveTime = cacheDefaults != null ? cacheDefaults.cacheLiveTime() : 0;
        }
        Object obj;
        chain.doChain();
        obj = chain.getReturn();
        redisService().set((cacheName + ":" + cacheKey).getBytes(), Lang.toBytes(obj));
        if (liveTime > 0) {
            redisService().expire((cacheName + ":" + cacheKey).getBytes(), liveTime);
        }
        chain.setReturnValue(obj);
    }
}
