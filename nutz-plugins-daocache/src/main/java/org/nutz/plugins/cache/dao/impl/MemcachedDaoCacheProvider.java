package org.nutz.plugins.cache.dao.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.nutz.plugins.cache.dao.DaoCacheProvider;

public class MemcachedDaoCacheProvider implements DaoCacheProvider {

	MemcachedClient client;
	
	public void init() throws IOException {
		if (client == null) {
			List<InetSocketAddress> addrs = AddrUtil.getAddresses("localhost:11211");
			XMemcachedClientBuilder builder = new XMemcachedClientBuilder(addrs);
			builder.setCommandFactory(new BinaryCommandFactory());
			client = builder.build();
		}
	}

	public void depose() throws Throwable {
		if (client != null)
			client.shutdown();
	}

	public Object get(String cacheName, String key) {
		return null;
	}

	public boolean put(String cacheName, String key, Object obj) {
		return false;
	}

	public void remove(String cacheName, String key) {
		
	}

	public boolean exists(String cacheName, String key) {
		return false;
	}

	public void clear(String cacheName) {
	}

}
