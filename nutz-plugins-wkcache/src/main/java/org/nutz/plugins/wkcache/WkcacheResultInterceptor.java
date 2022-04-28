package org.nutz.plugins.wkcache;

import org.nutz.aop.InterceptorChain;
import org.nutz.el.El;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.MethodParamNamesScaner;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheResult;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean(singleton = false)
public class WkcacheResultInterceptor extends AbstractWkcacheInterceptor {
    private String cacheKey;
    private String cacheName;
    private int liveTime;
    private boolean ignoreNull;
    private boolean isHash;

    public void prepare(CacheDefaults cacheDefaults, CacheResult cacheResult, Method method) {
        cacheKey = Strings.sNull(cacheResult.cacheKey());
        cacheName = Strings.sNull(cacheResult.cacheName());
        liveTime = cacheResult.cacheLiveTime();
        ignoreNull = cacheResult.ignoreNull();
        isHash = cacheDefaults != null && cacheDefaults.isHash();
        if (Strings.isBlank(cacheName)) {
            cacheName = cacheDefaults != null ? cacheDefaults.cacheName() : "wk";
        }
        if (liveTime == 0) {
            liveTime = cacheDefaults != null ? cacheDefaults.cacheLiveTime() : 0;
        }
        if (getConf() != null && getConf().size() > 0) {
            int confLiveTime = getConf().getInt("wkcache." + cacheName, 0);
            if (confLiveTime > 0)
                liveTime = confLiveTime;
        }
    }

    public void filter(InterceptorChain chain) throws Throwable {
        Method method = chain.getCallingMethod();
        if (Strings.isBlank(cacheKey)) {
            cacheKey = method.getDeclaringClass().getName()
                    + "."
                    + method.getName()
                    + "#"
                    + Arrays.toString(chain.getArgs());
        } else {
            CharSegment key = new CharSegment(cacheKey);
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
                for (String val : key.keys()) {
                    _ctx.set(val, new El(val).eval(ctx));
                }
                cacheKey = key.render(_ctx).toString();
            } else {
                cacheKey = key.getOrginalString();
            }
        }
        Object obj;
        Jedis jedis = null;
        try {
            jedis = getJedisAgent().jedis();
            if (isHash) {
                byte[] bytes = jedis.hget(cacheName.getBytes(), cacheKey.getBytes());
                if (bytes == null) {
                    chain.doChain();
                    obj = chain.getReturn();
                    // 如果忽略空值，那么不缓存结果
                    if (null == obj && ignoreNull) {
                        chain.setReturnValue(null);
                        return;
                    }
                    jedis.hset(cacheName.getBytes(), cacheKey.getBytes(), Lang.toBytes(obj));
                } else {
                    try {
                        obj = Lang.fromBytes(bytes, method.getReturnType());
                    } catch (Exception e) {
                        //对象转换失败则清除缓存
                        jedis.hdel(cacheName.getBytes(), cacheKey.getBytes());
                        obj = chain.getReturn();
                        e.printStackTrace();
                    }
                }
            } else {
                byte[] bytes = jedis.get((cacheName + ":" + cacheKey).getBytes());
                if (bytes == null) {
                    chain.doChain();
                    obj = chain.getReturn();
                    // 如果忽略空值，那么不缓存结果
                    if (null == obj && ignoreNull) {
                        chain.setReturnValue(null);
                        return;
                    }
                    if (liveTime > 0) {
                        jedis.setex((cacheName + ":" + cacheKey).getBytes(), liveTime, Lang.toBytes(obj));
                    } else {
                        jedis.set((cacheName + ":" + cacheKey).getBytes(), Lang.toBytes(obj));
                    }
                } else {
                    try {
                        obj = Lang.fromBytes(bytes, method.getReturnType());
                    } catch (Exception e) {
                        //对象转换失败则清除缓存
                        jedis.del((cacheName + ":" + cacheKey).getBytes());
                        obj = chain.getReturn();
                        e.printStackTrace();
                    }
                }
            }
        } finally {
            Streams.safeClose(jedis);
        }
        chain.setReturnValue(obj);
    }
}
