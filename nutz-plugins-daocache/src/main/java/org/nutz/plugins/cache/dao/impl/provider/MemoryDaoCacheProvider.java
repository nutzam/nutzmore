package org.nutz.plugins.cache.dao.impl.provider;

import java.util.concurrent.ConcurrentHashMap;

import org.nutz.repo.cache.simple.LRUCache;

/**
 * 基于内存的缓存实现, 默认缓存1000个对象
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class MemoryDaoCacheProvider extends AbstractDaoCacheProvider {

	ConcurrentHashMap<String, LRUCache<String, Object>> caches = new ConcurrentHashMap<String, LRUCache<String,Object>>();
	
	protected byte[] lock = new byte[0];
	
	/**
	 * 每个cache缓存的对象数
	 */
	protected int cacheSize = 1000;

	public Object get(String cacheName, String key) {
		return getSerializer().back(_getCache(cacheName).get(key));
	}

	public boolean put(String cacheName, String key, Object obj) {
		Object data = getSerializer().from(obj);
		if (data == null)
			return false;
		_getCache(cacheName).put(key, data);
		return false;
	}

	public void clear(String cacheName) {
		_getCache(cacheName).clear();
	}

	public LRUCache<String,Object> _getCache(String cacheName) {
	    LRUCache<String, Object> cache = caches.get(cacheName);
		if (cache == null) {
			synchronized (lock) {
				cache = caches.get(cacheName);
				if (cache == null) {
					cache = new LRUCache<String, Object>(cacheSize);
					caches.put(cacheName, cache);
				}
			}
		}
		return cache;
	}
}
