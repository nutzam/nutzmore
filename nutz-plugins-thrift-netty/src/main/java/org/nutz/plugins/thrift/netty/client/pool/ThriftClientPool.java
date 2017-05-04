package org.nutz.plugins.thrift.netty.client.pool;

import java.net.InetSocketAddress;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.thrift.protocol.TProtocolFactory;
import org.nutz.plugins.thrift.netty.common.pool.Pool;

import com.facebook.swift.service.ThriftClientManager;

/**
 * @author rekoe
 *
 */
public class ThriftClientPool<T extends AutoCloseable> extends Pool<T> {

	private final ThriftClientManager clientManager = new ThriftClientManager();

	public ThriftClientPool(ThriftClientConfig poolConfig, InetSocketAddress socketAddress, Class<T> type,
			TProtocolFactory protocolFactory) {
		this.internalPool = new GenericObjectPool<T>(
				new ThriftClientFactory<T>(clientManager, socketAddress, type, poolConfig, protocolFactory),
				poolConfig.getPoolConfig());
	}

	public ThriftClientPool(ThriftClientConfig poolConfig, InetSocketAddress socketAddress, Class<T> type) {
		this.internalPool = new GenericObjectPool<T>(
				new ThriftClientFactory<T>(clientManager, socketAddress, type, poolConfig), poolConfig.getPoolConfig());
	}

	@Override
	public void close() {
		super.close();
		this.clientManager.close();
	}

}
