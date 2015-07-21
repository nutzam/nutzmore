package org.nutz.plugins.cache.dao.impl.provider;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.CacheResult;
import org.nutz.plugins.cache.dao.CachedNutDaoExecutor;

import redis.clients.jedis.Jedis;

public class RedisDaoCacheProvider extends AbstractDaoCacheProvider {

    private static final Log log = Logs.get();
    
    protected Jedis jedis;
    
    protected String evalkey;
    
    protected byte[] _evalkey;
    
    protected String script;
    
    protected int expire;

    public Object get(String cacheName, String key) {
        byte[] obj = jedis.get((cacheName + ":" + key).getBytes());
        if (obj != null) {
            return getSerializer().back(obj);
        }
        return CacheResult.NOT_FOUNT;
    }

    public boolean put(String cacheName, String key, Object obj) {
        Object data = getSerializer().from(obj);
        if (data == null) {
            if (CachedNutDaoExecutor.DEBUG)
                log.debug("Serializer.from >> NULL");
            return false;
        }
        if (CachedNutDaoExecutor.DEBUG)
            log.debugf("CacheName=%s, KEY=%s", cacheName, key);
        byte[] _key = (cacheName + ":" + key).getBytes();
        if (expire > 0)
            jedis.setex(_key, expire, _key);
        else
            jedis.set(_key, (byte[])data);
        return true;
    }

    public void clear(String cacheName) {
        jedis.evalsha(_evalkey, 1, cacheName.getBytes());
    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }
    
    public void init() throws Throwable {
        super.init();
        if (script == null && evalkey == null) {
            script = "redis.call('del', unpack(redis.call('keys', KEYS[1] .. ':*')))";
            log.debug("use default clear script => " + script);
        }
        if (evalkey == null) {
            setEvalkey(jedis.scriptLoad(script));
        }
    }
    
    public void setEvalkey(String evalkey) {
        this.evalkey = evalkey;
        this._evalkey = evalkey.getBytes();
    }
}
