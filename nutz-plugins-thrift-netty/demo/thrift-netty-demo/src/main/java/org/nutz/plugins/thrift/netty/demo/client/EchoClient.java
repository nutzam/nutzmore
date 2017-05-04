package org.nutz.plugins.thrift.netty.demo.client;

import java.net.InetSocketAddress;

import org.nutz.plugins.thrift.netty.client.pool.ThriftClientConfig;
import org.nutz.plugins.thrift.netty.client.pool.ThriftClientPool;
import org.nutz.plugins.thrift.netty.common.pool.Pool;
import org.nutz.plugins.thrift.netty.demo.api.Echo;

/**
 * @author rekoe
 *
 */
public class EchoClient {

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ThriftClientConfig config = new ThriftClientConfig();
		InetSocketAddress socketAddress = new InetSocketAddress("localhost", 8083);
		final Pool<Echo> pool = new ThriftClientPool<Echo>(config, socketAddress, Echo.class);
		Echo client = null;
		try {
			client = pool.getResource();
			System.out.println(client.echo("aaa"));
		} catch (Throwable t) {
			pool.returnBrokenResource(client);
			client = null;
		} finally {
			pool.returnResource(client);
		}
		pool.close();
	}
}
