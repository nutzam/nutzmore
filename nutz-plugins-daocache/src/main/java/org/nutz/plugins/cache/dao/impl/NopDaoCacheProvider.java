package org.nutz.plugins.cache.dao.impl;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.DaoCacheProvider;

public class NopDaoCacheProvider implements DaoCacheProvider {
	
	private static final Log log = Logs.get();

	public void init() {
		log.debug("init ...");
	}

	public void depose() {
	}

	public Object get(String cacheName, String key) {
		log.debugf("cacheName=%s key=%s", cacheName, key);
		return null;
	}

	public boolean put(String cacheName, String key, Object obj) {
		log.debugf("cacheName=%s key=%s", cacheName, key);
		return false;
	}

	public void clear(String cacheName) {
		log.debugf("cacheName=%s", cacheName);
	}

}
