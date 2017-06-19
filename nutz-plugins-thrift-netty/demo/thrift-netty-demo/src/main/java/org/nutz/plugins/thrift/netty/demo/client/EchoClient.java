package org.nutz.plugins.thrift.netty.demo.client;

import java.net.InetSocketAddress;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.nutz.plugins.thrift.netty.client.pool.ThriftClientConfig;
import org.nutz.plugins.thrift.netty.client.pool.ThriftClientPool;
import org.nutz.plugins.thrift.netty.common.pool.Pool;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.duplex.TDuplexProtocolFactory;
import com.facebook.swift.service.ThriftClientManager;
import com.sada.common.thrift.TSegmentService;
import com.sada.common.thrift.TSegmentService1;

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
		new EchoClient().remoteTsTest();
	}

	public void localPollTest() {
		ThriftClientConfig config = new ThriftClientConfig();
		InetSocketAddress socketAddress = new InetSocketAddress("localhost", 17424);
		final Pool<TSegmentService> pool = new ThriftClientPool<TSegmentService>(config, socketAddress, TSegmentService.class, new TCompactProtocol.Factory());
		TSegmentService client = null;
		try {
			client = pool.getResource();
			System.out.println(client.getArabicWords("ترجمة اقوى الشجارات و الحوارات التي حدثت بين نجوم كرة القدم!!! (الجزء الثامن)'"));
		} catch (Throwable t) {
			pool.returnBrokenResource(client);
			client = null;
		} finally {
			pool.returnResource(client);
		}
		pool.close();
	}

	public void localTest() {
		try {
			TTransport transport = new TSocket("localhost", 17424);
			transport.open();
			TProtocol protocol = new TCompactProtocol(transport);
			org.nutz.plugins.thrift.netty.demo.api.Echo.Client client = new org.nutz.plugins.thrift.netty.demo.api.Echo.Client(protocol);
			System.out.println(client.echo("ddddd"));
			transport.close();
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException x) {
			x.printStackTrace();
		}
	}

	public void TSegmentServiceTest() {
		try {
			TTransport transport = new TSocket("localhost", 17424);
			transport.open();
			TProtocol protocol = new TCompactProtocol(transport);
			TSegmentService1.Client client = new TSegmentService1.Client(protocol);
			System.out.println(client.getArabicWords("ترجمة اقوى الشجارات و الحوارات التي حدثت بين نجوم كرة القدم!!! (الجزء الثامن)'"));
			transport.close();
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException x) {
			x.printStackTrace();
		}
	}

	public void remoteTSegmentTest() throws TException {
		ThriftClientConfig config = new ThriftClientConfig();
		InetSocketAddress socketAddress = new InetSocketAddress("localhost", 17424);
		final Pool<TSegmentService> pool = new ThriftClientPool<TSegmentService>(config, socketAddress, TSegmentService.class, new TCompactProtocol.Factory());
		TSegmentService client = null;
		try {
			client = pool.getResource();
			System.out.println(client.getArabicWords("ترجمة اقوى الشجارات و الحوارات التي حدثت بين نجوم كرة القدم!!! (الجزء الثامن)'"));
		} catch (Throwable t) {
			pool.returnBrokenResource(client);
			client = null;
		} finally {
			pool.returnResource(client);
		}
		pool.close();
	}

	public void remoteTsTest() throws Exception {
		try (ThriftClientManager manager = new ThriftClientManager(); TSegmentService ts = manager.createClient(new FramedClientConnector(new InetSocketAddress("localhost", 17424), TDuplexProtocolFactory.fromSingleFactory(new TCompactProtocol.Factory())), TSegmentService.class).get()) {
			System.out.println(ts.getArabicWords("aaa"));
		}
	}
}
