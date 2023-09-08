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
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.ScanParams;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
                String lua = "local hashKey = KEYS[1]\n" +
                        "local prefix = ARGV[1]\n" +
                        "local fields = redis.call('HKEYS', hashKey)\n" +
                        "for _, field in ipairs(fields) do\n" +
                        "    if string.sub(field, 1, string.len(prefix)) == prefix then\n" +
                        "        redis.call('HDEL', hashKey, field)\n" +
                        "    end\n" +
                        "end";
                if (getJedisAgent().isClusterMode()) {
                    JedisCluster jedisCluster = getJedisAgent().getJedisClusterWrapper().getJedisCluster();
                    for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                        try (Jedis jedis = pool.getResource()) {
                            jedis.eval(lua, Collections.singletonList(cacheName), Collections.singletonList(cacheKey.substring(0, cacheKey.lastIndexOf("*"))));
                        } catch (Exception e){
                            //只读节点可能报错,忽略之
                        }
                    }
                } else {
                    Jedis jedis = null;
                    try {
                        jedis = getJedisAgent().jedis();
                        jedis.eval(lua, Collections.singletonList(cacheName), Collections.singletonList(cacheKey.substring(0, cacheKey.lastIndexOf("*"))));
                    } finally {
                        Streams.safeClose(jedis);
                    }
                }
            } else {
                String lua = "local keysToDelete = redis.call('KEYS', ARGV[1])\n" +
                        "for _, key in ipairs(keysToDelete) do\n" +
                        "    redis.call('DEL', key)\n" +
                        "end";
                if (getJedisAgent().isClusterMode()) {
                    JedisCluster jedisCluster = getJedisAgent().getJedisClusterWrapper().getJedisCluster();
                    for (JedisPool pool : jedisCluster.getClusterNodes().values()) {
                        try (Jedis jedis = pool.getResource()) {
                            jedis.eval(lua, 0, cacheName + ":" + cacheKey);
                        } catch (Exception e){
                            //只读节点可能报错,忽略之
                        }
                    }
                } else {
                    Jedis jedis = null;
                    try {
                        jedis = getJedisAgent().jedis();
                        jedis.eval(lua, 0, cacheName + ":" + cacheKey);
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
