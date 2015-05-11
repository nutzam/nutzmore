package org.nutz.plugins.cache.dao.impl.provider;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.api.DaoCacheProvider;

/**
 * 无任何缓存操作的实现,但会打印各种log, 供debug用的实现
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class DaoCacheProviderWrapper implements DaoCacheProvider {
	
	private static final Log log = Logs.get();
	
	protected DaoCacheProvider proxy;
	
	public DaoCacheProviderWrapper(DaoCacheProvider proxy) {
	    this.proxy = proxy;
	}

	public void init() throws Throwable {
		log.debug("init ...");
		if (proxy != null)
		    proxy.init();
	}

	public void depose() throws Throwable {
	    log.debug("depose ...");
	    if (proxy != null)
	        proxy.depose();
	}

	public Object get(String cacheName, String key) {
		log.debugf("cacheName=%s key=%s", cacheName, key);
		if (proxy != null)
		    return proxy.get(cacheName, key);
		return null;
	}

	public boolean put(String cacheName, String key, Object obj) {
		log.debugf("cacheName=%s key=%s", cacheName, key);
		if (proxy != null)
		    return proxy.put(cacheName, key, obj);
		return false;
	}

	public void clear(String cacheName) {
		log.debugf("cacheName=%s", cacheName);
		if (proxy != null)
		    proxy.clear(cacheName);
	}

}
