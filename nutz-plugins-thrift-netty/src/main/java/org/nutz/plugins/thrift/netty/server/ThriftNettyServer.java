package org.nutz.plugins.thrift.netty.server;

import org.nutz.plugins.thrift.netty.server.codec.ThriftNettyDecoder;
import org.nutz.plugins.thrift.netty.server.codec.ThriftNettyEncoder;
import org.nutz.plugins.thrift.netty.server.configure.ThriftNettyServerDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author rekoe
 *
 */
public class ThriftNettyServer {

	private static final Logger logger = LoggerFactory.getLogger(ThriftNettyServer.class);

	private final ThriftNettyServerDef def;
	private EventLoopGroup bossGroup = null;
	private EventLoopGroup workerGroup = null;

	public ThriftNettyServer(ThriftNettyServerDef def) {
		this.def = def;
	}

	public void start() throws InterruptedException {
		bossGroup = new NioEventLoopGroup(def.getBossEventLoopCount());
		workerGroup = new NioEventLoopGroup(def.getWorkerEventLoopCount());
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workerGroup).localAddress(def.getServerPort()).channel(NioServerSocketChannel.class)
				.childHandler(new ChannelInitializer<Channel>() {

					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast("decode", new ThriftNettyDecoder(def.getMaxFrameSize(), def.getProtocolFactory()))
								.addLast("encode", new ThriftNettyEncoder(def.getMaxFrameSize()))
								.addLast("dispatch", new ThriftNettyDispatcher(def));
					}

				});
		logger.info(def.getName() + " " + "listen port : " + def.getServerPort());
		bootstrap.bind().sync();
	}

	public void stop() throws InterruptedException {
		bossGroup.shutdownGracefully().sync();
		workerGroup.shutdownGracefully().sync();
	}

}
