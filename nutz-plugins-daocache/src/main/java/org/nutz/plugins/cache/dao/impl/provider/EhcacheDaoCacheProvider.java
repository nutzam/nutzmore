package org.nutz.plugins.cache.dao.impl.provider;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Ehcache实现, 需要注入CacheManager
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class EhcacheDaoCacheProvider extends AbstractDaoCacheProvider {
    
    protected CacheManager cacheManager;

    public Object get(String cacheName, String key) {
        Element ele = getCache(cacheName).get(key);
        if (ele == null)
            return null;
        return getSerializer().back(ele.getObjectValue());
    }

    public boolean put(String cacheName, String key, Object obj) {
        Object data = getSerializer().from(obj);
        getCache(cacheName).put(new Element(key, data));
        return true;
    }

    public void clear(String cacheName) {
        getCache(cacheName).removeAll();
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    protected Cache getCache(String name) {
        return cacheManager.getCache(name);
    }
}
