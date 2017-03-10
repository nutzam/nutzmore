package org.nutz.plugins.thrift.netty.client.pool;

import java.net.InetSocketAddress;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.swift.service.ThriftClientEventHandler;
import com.facebook.swift.service.ThriftClientManager;
import com.google.common.collect.ImmutableList;

/**
 * @author rekoe
 *
 */
public class ThriftClientFactory<T extends AutoCloseable> implements PooledObjectFactory<T> {
    
    private final static int MAX_FRAME_SIZE = 16777216;
    private final static String DEFAULT_NAME = "default";

	private final ThriftClientManager clientManager;
	private final InetSocketAddress socketAddress;
	private final Class<T> type;
	private final ThriftClientConfig config;

	public ThriftClientFactory(ThriftClientManager clientManager, 
	                           InetSocketAddress socketAddress, 
	                           Class<T> type, 
	                           ThriftClientConfig config) {
		this.clientManager = clientManager;
		this.socketAddress = socketAddress;
		this.type = type;
		this.config = config;
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
		FramedClientConnector connector = new FramedClientConnector(socketAddress);
		return new DefaultPooledObject<T>(clientManager.createClient(
		        connector, 
		        type, 
		        config.getConnectTimeout(),
		        config.getReceiveTimeout(),
		        config.getReadTimeout(),
		        config.getWriteTimeout(),
		        MAX_FRAME_SIZE,
		        DEFAULT_NAME,
		        ImmutableList.<ThriftClientEventHandler>of(),
                clientManager.getDefaultSocksProxy()).get());
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
