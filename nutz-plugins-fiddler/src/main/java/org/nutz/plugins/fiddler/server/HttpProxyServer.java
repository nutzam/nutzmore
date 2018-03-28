package org.nutz.plugins.fiddler.server;

import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.fiddler.crt.CertUtil;
import org.nutz.plugins.fiddler.exception.HttpProxyExceptionHandle;
import org.nutz.plugins.fiddler.handler.HttpProxyServerHandle;
import org.nutz.plugins.fiddler.intercept.CertDownIntercept;
import org.nutz.plugins.fiddler.intercept.HttpProxyIntercept;
import org.nutz.plugins.fiddler.intercept.HttpProxyInterceptInitializer;
import org.nutz.plugins.fiddler.intercept.HttpProxyInterceptPipeline;
import org.nutz.plugins.fiddler.proxy.ProxyConfig;
import org.nutz.plugins.fiddler.proxy.ProxyType;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class HttpProxyServer {

	private final static Log log = Logs.get();
	// http代理隧道握手成功
	public final static HttpResponseStatus SUCCESS = new HttpResponseStatus(200, "Connection established");

	private HttpProxyServerConfig serverConfig = new HttpProxyServerConfig();;
	private HttpProxyInterceptInitializer proxyInterceptInitializer = new HttpProxyInterceptInitializer();
	private HttpProxyExceptionHandle httpProxyExceptionHandle = new HttpProxyExceptionHandle();
	private ProxyConfig proxyConfig;

	public HttpProxyServer() {
		// InternalLoggerFactory.setDefaultFactory(Log4J2LoggerFactory.INSTANCE);
		try {
			init();
		} catch (Exception e) {
			log.error(e);
		}
	}

	public SslContext getClientSslContext() {
		return serverConfig.getClientSslCtx();
	}

	private void init() throws Exception {
		// 注册BouncyCastleProvider加密库
		Security.addProvider(new BouncyCastleProvider());
		serverConfig.setClientSslCtx(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build());
		X509Certificate certificate = CertUtil.loadCert(Streams.fileIn("ca.crt"));
		// 读取CA证书使用者信息
		serverConfig.setIssuer(CertUtil.getSubject(certificate));
		// 读取CA证书有效时段(server证书有效期超出CA证书的，在手机上会提示证书不安全)
		serverConfig.setCaNotBefore(certificate.getNotBefore());
		serverConfig.setCaNotAfter(certificate.getNotAfter());
		// CA私钥用于给动态生成的网站SSL证书签证
		serverConfig.setCaPriKey(CertUtil.loadPriKey(Streams.fileIn("ca_private.der")));
		// 生产一对随机公私钥用于网站SSL证书动态创建
		KeyPair keyPair = CertUtil.genKeyPair();
		serverConfig.setServerPriKey(keyPair.getPrivate());
		serverConfig.setServerPubKey(keyPair.getPublic());
		serverConfig.setLoopGroup(new NioEventLoopGroup());
	}

	public HttpProxyServer serverConfig(HttpProxyServerConfig serverConfig) {
		this.serverConfig = serverConfig;
		return this;
	}

	public HttpProxyServer proxyInterceptInitializer(HttpProxyInterceptInitializer proxyInterceptInitializer) {
		this.proxyInterceptInitializer = proxyInterceptInitializer;
		return this;
	}

	public HttpProxyServer httpProxyExceptionHandle(HttpProxyExceptionHandle httpProxyExceptionHandle) {
		this.httpProxyExceptionHandle = httpProxyExceptionHandle;
		return this;
	}

	public HttpProxyServer proxyConfig(ProxyConfig proxyConfig) {
		this.proxyConfig = proxyConfig;
		return this;
	}

	public void start(int port) {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.DEBUG)).childHandler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel ch) throws Exception {
					ch.pipeline().addLast("logging", new LoggingHandler(LogLevel.DEBUG));
					ch.pipeline().addLast("httpCodec", new HttpServerCodec());
					ch.pipeline().addLast("serverHandle", new HttpProxyServerHandle(serverConfig, proxyInterceptInitializer, proxyConfig, httpProxyExceptionHandle));
				}
			});
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch (Exception e) {
			log.error(e);
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		new HttpProxyServer().proxyConfig(new ProxyConfig(ProxyType.HTTP, "127.0.0.1", 1087)).proxyInterceptInitializer(new HttpProxyInterceptInitializer() {
			@Override
			public void init(HttpProxyInterceptPipeline pipeline) {
				pipeline.addLast(new CertDownIntercept());
				pipeline.addLast(new HttpProxyIntercept());
			}
		}).httpProxyExceptionHandle(new HttpProxyExceptionHandle() {
			@Override
			public void beforeCatch(Channel clientChannel, Throwable cause) {
				super.beforeCatch(clientChannel, cause);
			}

			@Override
			public void afterCatch(Channel clientChannel, Channel proxyChannel, Throwable cause) {
				super.afterCatch(clientChannel, proxyChannel, cause);
			}
		}).start(8888);
	}

}
