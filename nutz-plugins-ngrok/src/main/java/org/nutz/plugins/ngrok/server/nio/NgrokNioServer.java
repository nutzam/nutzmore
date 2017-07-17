package org.nutz.plugins.ngrok.server.nio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javax.net.ssl.SSLEngine;

import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.ngrok.common.NgrokAgent;
import org.nutz.plugins.ngrok.common.NgrokMsg;
import org.nutz.plugins.ngrok.server.NgrokServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;

public class NgrokNioServer extends NgrokServer {

    private static final Log log = Logs.get();

    public AttributeKey<Integer> Attr_Message_Length = AttributeKey.valueOf("MessageLength");
    
    public Map<String, NgrokContrlHandler> clientHanlders = new HashMap<String, NgrokNioServer.NgrokContrlHandler>();
    
    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        final SSLEngine engine = buildSSLContext().createSSLEngine();
        engine.setUseClientMode(false);
        engine.setNeedClientAuth(false);
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline pipe = ch.pipeline();
                     pipe.addFirst("ssl", new SslHandler(engine));
                     pipe.addLast("ngrok.decode", new NgrokMessageDecoder());
                     pipe.addLast("ngrok.encode", new NgrokMessageEncoder());
                     pipe.addLast("ngrok.handler", new NgrokContrlHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
    
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(srv_port).sync(); // (7)
            
            // 再启动一个端口, 监听http请求
            ServerBootstrap b2 = new ServerBootstrap(); // (2)
            b2.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline pipe = ch.pipeline();
                     pipe.addLast("ngrok.http", new NgrokHttpHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
    
            // Bind and start to accept incoming connections.
            ChannelFuture f2 = b2.bind(http_port).sync(); // (7)
    
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
            f2.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    
    class NgrokMessageDecoder extends ByteToMessageDecoder {

        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws IOException {
            Integer len = ctx.channel().attr(Attr_Message_Length).get();
            if (len != null && len > 0) {
                if (in.isReadable(len)) {
                    out.add(NgrokAgent.readMsg(in.toString(Encoding.CHARSET_UTF8)));
                    in.readBytes(len);
                    ctx.channel().attr(Attr_Message_Length).set(0);
                    return; // 继续下一条消息
                }
                return; // 等数据
            }
            if (in.isReadable(8)) {
                len = (int) in.readLongLE();
                log.info("len="+len);
                ctx.channel().attr(Attr_Message_Length).set(len);
            }
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
        
    }
    
    class NgrokMessageEncoder extends MessageToByteEncoder<NgrokMsg> {

        protected void encode(ChannelHandlerContext ctx, NgrokMsg msg, ByteBuf out)
                throws Exception {
            NgrokAgent.writeMsg(new ByteBufOutputStream(out), msg);
        }
        
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
    
    class NgrokContrlHandler extends ChannelInboundHandlerAdapter {
        
        String id;
        
        boolean authed;
        
        boolean gzip_proxy;
        
        NgrokMsg authMsg;
        
        long lastPing;
        
        boolean proxyMode;
        
        public ArrayBlockingQueue<ChannelHandlerContext> idleProxys = new ArrayBlockingQueue<ChannelHandlerContext>(128);
        public ArrayBlockingQueue<NgrokHttpHandler> waitProxys = new ArrayBlockingQueue<NgrokHttpHandler>(128);
        
        public void channelRead(ChannelHandlerContext ctx, Object _msg) throws Exception {
            NgrokMsg msg = (NgrokMsg)_msg;
            String type = msg.getType();
            log.debug("msg type = " + type);
            if ("Auth".equals(type)) {
                if (authed) {
                    ctx.writeAndFlush(NgrokMsg.authResp("", "Auth Again?!!"));
                    ctx.close();
                    return;
                }
                if (!auth.check(NgrokNioServer.this, msg)) {
                    ctx.writeAndFlush(NgrokMsg.authResp("", "AuthError"));
                    ctx.close();
                    return;
                }
                id = msg.getString("ClientId");
                if (Strings.isBlank(id))
                    id = R.UU32();
                gzip_proxy = msg.getBoolean("GzipProxy", false);
                if (log.isDebugEnabled())
                    log.debugf("New Client >> id=%s gzip_proxy=%s", id, gzip_proxy);
                ctx.writeAndFlush(NgrokMsg.authResp(id, ""));
                msg.put("ClientId", id);
                authMsg = msg;
                authed = true;
                lastPing = System.currentTimeMillis();
                clientHanlders.put(id, this);
            } else if ("ReqTunnel".equals(type)) {
                if (!authed) {
                    ctx.writeAndFlush(NgrokMsg.newTunnel("", "", "", "Not Auth Yet"));
                    ctx.close();
                    return;
                }
                String[] mapping = auth.mapping(NgrokNioServer.this,
                                                id, authMsg,
                                                msg);
                if (mapping == null || mapping.length == 0) {
                    ctx.writeAndFlush(NgrokMsg.newTunnel("", "", "", "pls check your token"));
                    ctx.close();
                    return;
                }
                String reqId = msg.getString("ReqId");
                for (String host : mapping) {
                    ctx.writeAndFlush(NgrokMsg.newTunnel(reqId, "http://" + host, "http", ""));
                    hostmap.put(host, id); // 冲突了怎么办?
                    reqIdMap.put(host, reqId);
                }
            } else if ("Ping".equals(type)) {
                ctx.writeAndFlush(NgrokMsg.pong());
            } else if ("Pong".equals(type)) {
                lastPing = System.currentTimeMillis();
            } else if ("RegProxy".equals(type)) {
                String clientId = msg.getString("ClientId");
                NgrokContrlHandler client = clientHanlders.get(clientId);
                if (client == null) {
                    log.debug("not such client id=" + clientId);
                    ctx.close();
                    return;
                }
                proxyMode = true;
                NgrokHttpHandler httpHandler = waitProxys.poll();
                if (httpHandler == null)
                    client.idleProxys.add(ctx);
                else {
                    startProxy(httpHandler.ctx, ctx, httpHandler.host, httpHandler.bao.toByteArray(), null);
                }
            } else {
                log.info("Bad Type=" + type);
            }
        }
        
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            cause.printStackTrace();
            ctx.close();
        }
    }
    
    class NgrokHttpHandler extends ByteToMessageDecoder {
        
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        
        ChannelHandlerContext proxy;
        ChannelHandlerContext ctx;
        boolean wait;
        String host;

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
                throws Exception {
            if (wait)
                return;
            if (proxy != null) {
                proxy.write(in);
                return;
            }
            
            int index = in.bytesBefore((byte) '\n');
            if (index == -1) {
                if (in.isReadable(8192)) {
                    ctx.close();
                }
                return;
            }
            if (index < 3) { // \r\n
                ctx.close(); // 读取Header已经结束,但host没找到? 干掉你.
                return;
            }
            byte[] buf = new byte[index + 1];
            in.readBytes(buf);
            bao.write(buf);
            if (index > 5) {
                String headerLine = new String(buf);
                if (headerLine.equalsIgnoreCase("host:") || headerLine.equalsIgnoreCase("host ")) {
                    String host = headerLine.substring(headerLine.indexOf(':'));
                    log.debug("Host : " + host);
                    String cliendId = hostmap.get(host.toLowerCase());
                    if (cliendId == null) {
                        log.debugf("Host[%s] without Ngrok Client Id", host);
                        ctx.writeAndFlush(ctx.channel().alloc().buffer().writeBytes("HTTP/1.0 404 Not Found\r\n\r\n".getBytes()));
                        ctx.close();
                        return;
                    }
                    NgrokContrlHandler handler = clientHanlders.get(cliendId);
                    if (handler == null) {
                        log.debugf("Host[%s] without Ngrok Client", host);
                        ctx.writeAndFlush(ctx.channel().alloc().buffer().writeBytes("HTTP/1.0 404 Not Found\r\n\r\n".getBytes()));
                        ctx.close();
                        return;
                    }
                    proxy = handler.idleProxys.poll();
                    if (proxy == null) {
                        String reqId = reqIdMap.get(host);
                        if (reqId == null) {
                            ctx.close();
                            return;
                        }
                        handler.waitProxys.add(this);
                        wait = true;
                        this.ctx = ctx;
                        this.host = host;
                        proxy.writeAndFlush(NgrokMsg.reqProxy(reqId, "http://" + host, "http", ""));
                        return;
                    } else {
                        startProxy(ctx, proxy, host, bao.toByteArray(), in);
                        bao = null;
                        return;
                    }
                }
            }
        }
        
    }
    
    public static void startProxy(ChannelHandlerContext ctx, ChannelHandlerContext proxy, String host, byte[] buf, ByteBuf in) {
        proxy.write(NgrokMsg.startProxy("http://" + host, ""));
        proxy.pipeline().remove("ngrok.encode");
        proxy.pipeline().remove("ngrok.decode");
        proxy.pipeline().remove("ngrok.handler");
        proxy.pipeline().addLast(new ChannelInboundHandlerAdapter() {
            public void channelRead(ChannelHandlerContext ctx, Object msg)
                    throws Exception {
                ctx.writeAndFlush(msg);
            }
        });
        proxy.write(proxy.alloc().buffer().writeBytes(buf));
        proxy.write(in);
    }
    
    public static void main(String[] args) throws Exception {
        NgrokNioServer server = new NgrokNioServer();
        if (!NgrokAgent.fixFromArgs(server, args)) {
            log.debug("usage : -srv_host=wendal.cn -srv_port=4443 -http_port=9080 -ssl_jks_path=wendal.cn.jks -ssl_jks_password=123456 -conf_file=xxx.properties");
        }
        server.start();
    }
}
