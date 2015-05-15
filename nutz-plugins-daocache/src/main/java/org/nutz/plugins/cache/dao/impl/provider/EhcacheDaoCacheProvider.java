package org.nutz.plugins.cache.dao.impl.provider;

import org.nutz.plugins.cache.dao.CacheResult;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Ehcache实现, 需要注入CacheManager
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class EhcacheDaoCacheProvider extends AbstractDaoCacheProvider {
    
    private byte[] lock = new byte[0];
    
    protected CacheManager cacheManager;

    public Object get(String cacheName, String key) {
        Element ele = getCache(cacheName, true).get(key);
        if (ele == null)
            return CacheResult.NOT_FOUNT;
        return getSerializer().back(ele.getObjectValue());
    }

    public boolean put(String cacheName, String key, Object obj) {
        Object data = getSerializer().from(obj);
        getCache(cacheName, true).put(new Element(key, data));
        return true;
    }

    public void clear(String cacheName) {
        Cache cache = getCache(cacheName, false);
        if (cache != null)
            cache.removeAll();
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    protected Cache getCache(String name, boolean create) {
        Cache cache = cacheManager.getCache(name);
        if (cache == null) {
            if (!create)
                return null;
            synchronized (lock) {
                cache = cacheManager.getCache(name);
                if (cache == null) {
                    cacheManager.addCache(name);
                    cache = cacheManager.getCache(name);
                }
            }
        }
        return cache;
    }
}
