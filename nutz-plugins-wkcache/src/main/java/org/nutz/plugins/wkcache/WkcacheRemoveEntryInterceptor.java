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
import java.util.Map;

/**
 * Created by wizzer on 2017/6/14.
 */
@IocBean(singleton = false)
public class WkcacheRemoveEntryInterceptor extends AbstractWkcacheInterceptor {
    private String cacheKeyTemp;
    private String cacheName;
    private boolean isHash;
    private List<String> paramNames;

    public void prepare(CacheDefaults cacheDefaults, CacheRemove cacheRemove, Method method) {
        cacheKeyTemp = Strings.sNull(cacheRemove.cacheKey());
        cacheName = Strings.sNull(cacheRemove.cacheName());
        isHash = cacheDefaults != null && cacheDefaults.isHash();
        if (Strings.isBlank(cacheName)) {
            cacheName = cacheDefaults != null ? cacheDefaults.cacheName() : "wk";
        }
        paramNames = MethodParamNamesScaner.getParamNames(method);
    }

    public void filter(InterceptorChain chain) throws Throwable {
        String cacheKey = cacheKeyTemp;
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
                if (paramNames != null) {
                    for (int i = 0; i < paramNames.size() && i < args.length; i++) {
                        ctx.set(paramNames.get(i), args[i]);
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
        if (cacheKey.contains(",")) {
            for (String key : cacheKey.split(",")) {
                delCache(cacheName, key);
            }
        } else {
            delCache(cacheName, cacheKey);
        }
        chain.doChain();
    }

    private void delCache(String cacheName, String cacheKey) {
        if (cacheKey.endsWith("*")) {
            if (isHash) {
                ScanParams match = new ScanParams().match(cacheKey);
                if (getJedisAgent().isClusterMode()) {
                    JedisCluster jedisCluster = getJedisAgent().getJedisClusterWrapper().getJedisCluster();
                    List<Map.Entry<String, String>> keys = new ArrayList<>();
                    ScanResult<Map.Entry<String, String>> scan = null;
                    do {
                        scan = jedisCluster.hscan(cacheName, scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                        keys.addAll(scan.getResult());
                    } while (!scan.isCompleteIteration());
                    Jedis jedis = null;
                    try {
                        jedis = getJedisAgent().jedis();
                        for (Map.Entry<String, String> key : keys) {
                            jedis.hdel(cacheName, key.getKey());
                        }
                    } finally {
                        Streams.safeClose(jedis);
                    }
                } else {
                    Jedis jedis = null;
                    try {
                        jedis = getJedisAgent().jedis();
                        ScanResult<Map.Entry<String, String>> scan = null;
                        do {
                            scan = jedis.hscan(cacheName, scan == null ? ScanParams.SCAN_POINTER_START : scan.getStringCursor(), match);
                            for (Map.Entry<String, String> key : scan.getResult()) {
                                jedis.hdel(cacheName, key.getKey());
                            }
                        } while (!scan.isCompleteIteration());
                    } finally {
                        Streams.safeClose(jedis);
                    }
                }
            } else {
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
            }
        } else {
            Jedis jedis = null;
            try {
                jedis = getJedisAgent().jedis();
                if (isHash) {
                    jedis.hdel(cacheName.getBytes(), cacheKey.getBytes());
                } else {
                    jedis.del((cacheName + ":" + cacheKey).getBytes());
                }
            } finally {
                Streams.safeClose(jedis);
            }
        }
    }

}
