package org.nutz.plugins.cache.dao.impl.provider;

import java.util.concurrent.ConcurrentHashMap;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.CacheResult;
import org.nutz.plugins.cache.dao.CachedNutDaoExecutor;
import org.nutz.repo.cache.simple.LRUCache;

/**
 * 基于内存的缓存实现, 默认缓存1000个对象
 * 
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class MemoryDaoCacheProvider extends AbstractDaoCacheProvider {

    private static final Log log = Logs.get();

    protected ConcurrentHashMap<String, LRUCache<String, Object>> caches = new ConcurrentHashMap<String, LRUCache<String, Object>>();

    /**
     * 每个cache缓存的对象数
     */
    protected int cacheSize = 1000;

    public Object get(String cacheName, String key) {
        LRUCache<String, Object> cache = _getCache(cacheName, false);
        if (cache != null) {
            return getSerializer().back(cache.get(key));
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
        _getCache(cacheName, true).put(key, data);
        return true;
    }

    public void clear(String cacheName) {
        LRUCache<String, Object> cache = _getCache(cacheName, false);
        if (cache != null)
            cache.clear();
    }

    public LRUCache<String, Object> _getCache(String cacheName, boolean create) {
        LRUCache<String, Object> cache = caches.get(cacheName);
        if (cache == null) {
            if (!create)
                return null;
            cache = new LRUCache<String, Object>(cacheSize);
            caches.put(cacheName, cache);
        }
        // log.debugf("Cache(%s) size=%s", cacheName, cache.getAll().size());
        return cache;
    }
    
    public void setCacheSize(int cacheSize) {
        this.cacheSize = cacheSize;
    }
}
