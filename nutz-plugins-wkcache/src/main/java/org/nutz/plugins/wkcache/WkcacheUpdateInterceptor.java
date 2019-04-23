package org.nutz.plugins.wkcache;

import org.nutz.aop.InterceptorChain;
import org.nutz.el.El;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.MethodParamNamesScaner;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheUpdate;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean(singleton = false)
public class WkcacheUpdateInterceptor extends AbstractWkcacheInterceptor {

    public void filter(InterceptorChain chain) throws Throwable {
        Method method = chain.getCallingMethod();
        CacheUpdate cacheResult = method.getAnnotation(CacheUpdate.class);
        String cacheKey = Strings.sNull(cacheResult.cacheKey());
        String cacheName = Strings.sNull(cacheResult.cacheName());
        int liveTime = cacheResult.cacheLiveTime();
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
                Object[] args = chain.getArgs();
                List<String> names = MethodParamNamesScaner.getParamNames(method);//不支持nutz低于1.60的版本
                if (names != null) {
                    for (int i = 0; i < names.size() && i < args.length; i++) {
                        ctx.set(names.get(i), args[i]);
                    }
                }
                ctx.set("args", args);
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
        }
        if (liveTime == 0) {
            CacheDefaults cacheDefaults = method.getDeclaringClass()
                    .getAnnotation(CacheDefaults.class);
            liveTime = cacheDefaults != null ? cacheDefaults.cacheLiveTime() : 0;
        }
        if (getConf() != null && getConf().size() > 0) {
            int confLiveTime = getConf().getInt("wkcache." + cacheName, 0);
            if (confLiveTime > 0)
                liveTime = confLiveTime;
        }
        Object obj;
        chain.doChain();
        obj = chain.getReturn();
        if (liveTime > 0) {
            redisService().setex((cacheName + ":" + cacheKey).getBytes(), liveTime, Lang.toBytes(obj));
        } else {
            redisService().set((cacheName + ":" + cacheKey).getBytes(), Lang.toBytes(obj));
        }
        chain.setReturnValue(obj);
    }
}
