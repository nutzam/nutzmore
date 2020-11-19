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
import org.nutz.plugins.wkcache.annotation.CacheRemove;
import redis.clients.jedis.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean(singleton = false)
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
        if (cacheKey.endsWith("*")) {
            ScanParams match = new ScanParams().match(cacheName + ":" + cacheKey);
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
        } else {
            Jedis jedis = null;
            try {
                jedis = getJedisAgent().jedis();
                jedis.del((cacheName + ":" + cacheKey).getBytes());
            } finally {
                Streams.safeClose(jedis);
            }
        }
        chain.doChain();
    }

}
