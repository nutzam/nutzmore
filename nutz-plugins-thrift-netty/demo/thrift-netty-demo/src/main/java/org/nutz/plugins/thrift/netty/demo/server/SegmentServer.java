package org.nutz.plugins.thrift.netty.demo.server;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

import com.sada.common.thrift.TSegmentService1;

public class SegmentServer {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) throws TTransportException {
		TServerSocket serverTransport = new TServerSocket(17424);
		TProtocolFactory proFactory = new TCompactProtocol.Factory();
		TProcessor processor = new TSegmentService1.Processor(new TSegmentServiceImpl());
		TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(serverTransport);
		tArgs.maxWorkerThreads(1000);
		tArgs.minWorkerThreads(10);
		tArgs.processor(processor);
		tArgs.protocolFactory(proFactory);
		TServer tServer = new TThreadPoolServer(tArgs);
		tServer.serve();
		serverTransport.close();
		tServer.stop();
	}
}
