package org.nutz.plugins.thrift.netty.client.pool;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.duplex.TDuplexProtocolFactory;
import com.facebook.swift.service.ThriftClient;
import com.facebook.swift.service.ThriftClientConfig;
import com.facebook.swift.service.ThriftClientManager;

/**
 * @author rekoe
 *
 */
public class ThriftClientFactory<T extends AutoCloseable> implements PooledObjectFactory<T> {

	private final ThriftClientManager clientManager;
	private final InetSocketAddress socketAddress;
	private final Class<T> type;
	private final org.nutz.plugins.thrift.netty.client.pool.ThriftClientConfig config;
	private TProtocolFactory protocolFactory;

	public ThriftClientFactory(ThriftClientManager clientManager, InetSocketAddress socketAddress, Class<T> type,
			org.nutz.plugins.thrift.netty.client.pool.ThriftClientConfig config) {
		this.clientManager = clientManager;
		this.socketAddress = socketAddress;
		this.type = type;
		this.config = config;
		this.protocolFactory = new TBinaryProtocol.Factory();
	}

	public ThriftClientFactory(ThriftClientManager clientManager, InetSocketAddress socketAddress, Class<T> type,
			org.nutz.plugins.thrift.netty.client.pool.ThriftClientConfig config, TProtocolFactory protocolFactory) {
		this.clientManager = clientManager;
		this.socketAddress = socketAddress;
		this.type = type;
		this.config = config;
		this.protocolFactory = protocolFactory;
	}

	@Override
	public void activateObject(PooledObject<T> pooledObject) throws Exception {
		//
	}

	@Override
	public void destroyObject(PooledObject<T> pooledObject) throws Exception {
		pooledObject.getObject().close();
	}

	@Override
	public PooledObject<T> makeObject() throws Exception {
		return new DefaultPooledObject<T>(createTSegmentServiceClient(clientManager, protocolFactory, type));
	}

	private T createTSegmentServiceClient(ThriftClientManager manager, TProtocolFactory protocolFactory, Class<T> type)
			throws ExecutionException, InterruptedException, TException {
		ThriftClientConfig config = new ThriftClientConfig().setConnectTimeout(this.config.getConnectTimeout())
				.setReceiveTimeout(this.config.getReceiveTimeout()).setReadTimeout(this.config.getReadTimeout())
				.setWriteTimeout(this.config.getWriteTimeout());
		ThriftClient<T> thriftClient = new ThriftClient<>(manager, type, config, type.getName());
		return thriftClient.open(
				new FramedClientConnector(socketAddress, TDuplexProtocolFactory.fromSingleFactory(protocolFactory)))
				.get();
	}

	@Override
	public void passivateObject(PooledObject<T> pooledObject) throws Exception {
		//
	}

	@Override
	public boolean validateObject(PooledObject<T> pooledObject) {
		return true;
	}

}
