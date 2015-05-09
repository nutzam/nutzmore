package org.nutz.plugins.cache.dao.impl.provider;

import java.util.concurrent.ConcurrentHashMap;

import org.nutz.plugins.cache.dao.impl.convert.JavaCacheSerializer;

public class MemoryDaoCacheProvider extends AbstractDaoCacheProvider {

	ConcurrentHashMap<String, ConcurrentHashMap<String, byte[]>> caches = new ConcurrentHashMap<String, ConcurrentHashMap<String,byte[]>>();
	
	protected byte[] lock = new byte[0];

	public void init() throws Throwable {
	    if (getSerializer() == null)
	        setSerializer(new JavaCacheSerializer());
	}

	public void depose() throws Throwable {
		caches = null;
	}

	public Object get(String cacheName, String key) {
		return getSerializer().back(_getCache(cacheName).get(key));
	}

	public boolean put(String cacheName, String key, Object obj) {
		byte[] data = (byte[]) getSerializer().from(obj);
		if (data == null)
			return false;
		_getCache(cacheName).put(key, data);
		return false;
	}

	public void clear(String cacheName) {
		_getCache(cacheName).clear();
	}

	public ConcurrentHashMap<String,byte[]> _getCache(String cacheName) {
		ConcurrentHashMap<String,byte[]> cache = caches.get(cacheName);
		if (cache == null) {
			synchronized (lock) {
				cache = caches.get(cacheName);
				if (cache == null) {
					cache = new ConcurrentHashMap<String, byte[]>();
					caches.put(cacheName, cache);
				}
			}
		}
		return cache;
	}
}
