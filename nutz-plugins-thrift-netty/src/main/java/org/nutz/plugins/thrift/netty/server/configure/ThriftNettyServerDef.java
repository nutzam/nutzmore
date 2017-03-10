package org.nutz.plugins.thrift.netty.server.configure;

import java.util.concurrent.Executor;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TProtocolFactory;


/**
 * @author rekoe
 *
 */
public class ThriftNettyServerDef {

	private final String name;
	private final int serverPort;
	private final int maxFrameSize;
	private final Executor taskExecutor;
	private final int bossEventLoopCount;
	private final int workerEventLoopCount;
	private final TProcessorFactory processorFactory;
	private final TProtocolFactory protocolFactory;

	public ThriftNettyServerDef(String name, int serverPort, int maxFrameSize,
			Executor taskExecutor, int bossEventLoopCount, int workerEventLoopCount,
			TProcessorFactory processorFactory, TProtocolFactory protocolFactory) {
		this.name = name;
		this.serverPort = serverPort;
		this.maxFrameSize = maxFrameSize;
		this.taskExecutor = taskExecutor;
		this.bossEventLoopCount = bossEventLoopCount;
		this.workerEventLoopCount = workerEventLoopCount;
		this.processorFactory = processorFactory;
		this.protocolFactory = protocolFactory;
	}

	public String getName() {
		return name;
	}

	public int getServerPort() {
		return serverPort;
	}

	public int getMaxFrameSize() {
		return maxFrameSize;
	}

	public Executor getTaskExecutor() {
		return taskExecutor;
	}

	public int getBossEventLoopCount() {
		return bossEventLoopCount;
	}

	public int getWorkerEventLoopCount() {
		return workerEventLoopCount;
	}

	public TProcessorFactory getProcessorFactory() {
		return processorFactory;
	}

	public TProtocolFactory getProtocolFactory() {
		return protocolFactory;
	}
	
}
