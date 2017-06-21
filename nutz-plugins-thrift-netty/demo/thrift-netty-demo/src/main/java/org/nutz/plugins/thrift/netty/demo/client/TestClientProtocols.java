/*
 * Copyright (C) 2012 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.nutz.plugins.thrift.netty.demo.client;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.nutz.plugins.thrift.netty.demo.server.EchoImpl;
import org.nutz.plugins.thrift.netty.server.ThriftNettyServer;
import org.nutz.plugins.thrift.netty.server.configure.ThriftNettyServerDefBuilder;

import com.facebook.nifty.client.FramedClientConnector;
import com.facebook.nifty.duplex.TDuplexProtocolFactory;
import com.facebook.nifty.processor.NiftyProcessor;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftClient;
import com.facebook.swift.service.ThriftClientConfig;
import com.facebook.swift.service.ThriftClientManager;
import com.facebook.swift.service.ThriftEventHandler;
import com.facebook.swift.service.ThriftServiceProcessor;
import com.google.common.collect.ImmutableList;
import com.google.common.net.HostAndPort;

import io.airlift.units.Duration;

public class TestClientProtocols {

	public void testUnmatchedProtocols() throws Exception {
		try (ScopedServer server = new ScopedServer(new TCompactProtocol.Factory(),8083); ThriftClientManager manager = new ThriftClientManager(); Echo client = createClient(manager, server, new TCompactProtocol.Factory())) {
			System.out.println(client.echo("ddddd"));
		}
	}


	private Echo createClient(ThriftClientManager manager, ScopedServer server, TProtocolFactory protocolFactory) throws ExecutionException, InterruptedException, TException {
		ThriftClientConfig config = new ThriftClientConfig().setConnectTimeout(Duration.valueOf("1s")).setReceiveTimeout(Duration.valueOf("10s")).setReadTimeout(Duration.valueOf("1s")).setWriteTimeout(Duration.valueOf("1s"));
		ThriftClient<Echo> thriftClient = new ThriftClient<>(manager, Echo.class, config, "EchoClient");
		return thriftClient.open(new FramedClientConnector(HostAndPort.fromParts("localhost", server.getPort()), TDuplexProtocolFactory.fromSingleFactory(protocolFactory))).get();
	}


	public static void main(String[] args) throws Exception {
		new TestClientProtocols().testUnmatchedProtocols();
	}

	private class ScopedServer implements AutoCloseable {
		private final ThriftNettyServer server;
		private final int port;
		public ScopedServer(TProtocolFactory protocolFactory,int port) throws Exception {
			this.port = port;
			//ThriftServiceProcessor processor = new ThriftServiceProcessor(new ThriftCodecManager(), ImmutableList.<ThriftEventHandler>of(), new EchoImpl());
			/*ThriftServerDef def = ThriftServerDef.newBuilder().listen(8080).withProcessor(processor).protocol(protocolFactory).build();
			server = new NettyServerTransport(def, NettyServerConfig.newBuilder().build(), new DefaultChannelGroup());
			server.start();*/
			List<Object> services = new ArrayList<>();
			services.add(new EchoImpl());
			final NiftyProcessor niftyProcessor = new ThriftServiceProcessor(new ThriftCodecManager(), ImmutableList.<ThriftEventHandler>of(), services);
			TProcessor processor = new TProcessor() {

				@Override
				public boolean process(TProtocol in, TProtocol out) throws TException {

					try {
						return niftyProcessor.process(in, out, null).get();
					} catch (Exception e) {
						throw new TException(e);
					}
				}

			};
			server = new ThriftNettyServer(new ThriftNettyServerDefBuilder().serverPort(port).protocolFactory(protocolFactory).processorFactory(new TProcessorFactory(processor)).build());
			server.start();
		}

		public int getPort() {
			return port;
		}
 
		@Override
		public void close() throws Exception {
			server.stop();
		}
	}
}
