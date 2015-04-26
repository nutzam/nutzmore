package org.nutz.plugins.cache.dao.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.DaoCacheProvider;

public class MemoryDaoCacheProvider implements DaoCacheProvider {
	
	private static final Log log = Logs.get();
	
	ConcurrentHashMap<String, ConcurrentHashMap<String, byte[]>> caches = new ConcurrentHashMap<String, ConcurrentHashMap<String,byte[]>>();
	
	protected byte[] lock = new byte[0];

	public void init() throws Throwable {
	}

	public void depose() throws Throwable {
		caches = null;
	}

	public Object get(String cacheName, String key) {
		return toObj(_getCache(cacheName).get(key));
	}

	public boolean put(String cacheName, String key, Object obj) {
		byte[] data = toBytes((Serializable) obj);
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
	
	protected byte[] toBytes(Serializable obj) {
		if (obj == null)
			return null;
		try {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bao);
			oos.writeObject(obj);
			return bao.toByteArray();
		} catch (Exception e) {
			log.info("Object to bytes fail", e);
			return null;
		}
	}
	
	protected Object toObj(byte[] data) {
		if (data == null)
			return null;
		try {
			return new ObjectInputStream(new ByteArrayInputStream(data)).readObject();
		} catch (Exception e) {
			log.info("bytes to Object fail", e);
			return null;
		}
	}
}
