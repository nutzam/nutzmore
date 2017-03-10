package org.nutz.plugins.thrift.netty.server.configure;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocolFactory;

/**
 * @author rekoe
 *
 */
public class ThriftNettyServerDefBuilder {

	public static final int DEFAULT_BOSS_THREAD_COUNT = 1;
	public static final int DEFAULT_WORKER_THREAD_COUNT = Runtime.getRuntime().availableProcessors() * 2;

	private String name;

	private int serverPort;

	private int maxFrameSize;

	private static final int MAX_FRAME_SIZE = 64 * 1024 * 1024;

	private Executor taskExecutor;

	private int bossEventLoopCount;

	private int workerEventLoopCount;

	private TProcessorFactory processorFactory = null;

	private TProtocolFactory protocolFactory;

	public ThriftNettyServerDefBuilder() {
		this.name = "ThriftNettyServer";
		this.serverPort = 8080;
		this.maxFrameSize = MAX_FRAME_SIZE;
		this.taskExecutor = Executors.newFixedThreadPool(DEFAULT_WORKER_THREAD_COUNT);
		this.bossEventLoopCount = DEFAULT_BOSS_THREAD_COUNT;
		this.workerEventLoopCount = DEFAULT_WORKER_THREAD_COUNT;
		this.protocolFactory = new TBinaryProtocol.Factory(true, true);
	}

	public ThriftNettyServerDefBuilder name(String name) {
		this.name = name;
		return this;
	}

	public ThriftNettyServerDefBuilder serverPort(int serverPort) {
		this.serverPort = serverPort;
		return this;
	}

	public ThriftNettyServerDefBuilder maxFrameSize(int maxFrameSize) {
		this.maxFrameSize = maxFrameSize;
		return this;
	}

	public ThriftNettyServerDefBuilder taskExecutor(Executor taskExecutor) {
		this.taskExecutor = taskExecutor;
		return this;
	}

	public ThriftNettyServerDefBuilder bossEventLoopCount(int bossEventLoopCount) {
		this.bossEventLoopCount = bossEventLoopCount;
		return this;
	}

	public ThriftNettyServerDefBuilder workerEventLoopCount(int workerEventLoopCount) {
		this.workerEventLoopCount = workerEventLoopCount;
		return this;
	}

	public ThriftNettyServerDefBuilder processorFactory(TProcessorFactory processorFactory) {
		this.processorFactory = processorFactory;
		return this;
	}

	public ThriftNettyServerDefBuilder protocolFactory(TProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory;
		return this;
	}

	public ThriftNettyServerDef build() {
		return new ThriftNettyServerDef(name, serverPort, maxFrameSize, taskExecutor, 
				bossEventLoopCount, workerEventLoopCount, processorFactory, protocolFactory);
	}
}
