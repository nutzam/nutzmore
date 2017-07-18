package org.nutz.plugins.ngrok.server.netty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import org.nutz.lang.Encoding;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.ngrok.common.NgrokAgent;
import org.nutz.plugins.ngrok.common.NgrokMsg;
import org.nutz.plugins.ngrok.server.AbstractNgrokServer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
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

/**
 * 基于Netty 4.1的Ngrok服务器端实现
 * @author wendal
 *
 */
public class NgrokNettyServer extends AbstractNgrokServer {

    private static final Log log = Logs.get();

    public static AttributeKey<Integer> Attr_Message_Length = AttributeKey.valueOf("MessageLength");

    public Map<String, NgrokContrlHandler> clientHanlders = new HashMap<String, NgrokNettyServer.NgrokContrlHandler>();
    
    public void start() throws Exception {
        // 首先,继续基本的初始化操作
        init();
        // netty的两个EventLoopGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // SSL引擎所需要的SSL上下文
        final SSLContext sslContext = buildSSLContext();
        try {
            // 首先建立控制链接, 4443端口
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                     ChannelPipeline pipe = ch.pipeline();
                     // SSL引擎必须每次新建,否则报ciphertext sanity check failed
                     SSLEngine engine = sslContext.createSSLEngine();
                     engine.setUseClientMode(false);
                     engine.setNeedClientAuth(false);
                     // 最外层是SSL套接字
                     pipe.addFirst("ssl", new SslHandler(engine));
                     // 然后是Ngrok消息解码器
                     pipe.addLast("ngrok.decode", new NgrokMessageDecoder());
                     // 接着是Ngrok消息编码器
                     pipe.addLast("ngrok.encode", new NgrokMessageEncoder());
                     // 最后是Ngrok业务处理器
                     pipe.addLast("ngrok.handler", new NgrokContrlHandler());
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 8192) // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // 绑定端口,开始启动
            ChannelFuture f = b.bind(srv_port).sync(); // (7)

            // 再启动一个端口, 监听http请求
            ServerBootstrap b2 = new ServerBootstrap(); // (2)
            b2.group(bossGroup, workerGroup)
              .channel(NioServerSocketChannel.class) // (3)
              .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                  @Override
                  public void initChannel(SocketChannel ch) throws Exception {
                      ChannelPipeline pipe = ch.pipeline();
                      // 仅需要一个ngrok.http处理器,解析Http请求中的host,然后对接Client
                      pipe.addLast("ngrok.http", new NgrokHttpHandler());
                  }
              })
              .option(ChannelOption.SO_BACKLOG, 8192) // (5)
              .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // 绑定端口,启动Http服务
            ChannelFuture f2 = b2.bind(http_port).sync(); // (7)

            // 然后就是瞎等.
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to
            // gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
            f2.channel().closeFuture().sync();
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     * Ngrok消息解码器, 消息格式是 8字节的长度数据, 然后一个Json字符串.
     * @author wendal
     *
     */
    class NgrokMessageDecoder extends ByteToMessageDecoder {

        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
                throws IOException {
            while (in.isReadable()) {
                // 看看前一条消息读完没
                Integer len = ctx.channel().attr(Attr_Message_Length).get();
                if (len == null || len == 0) {
                    // 前8个字节是长度,所以,起码等8个字节
                    if (in.isReadable(8)) {
                        // 读取长度
                        len = (int) in.readLongLE();
                        // 放入上下文
                        ctx.channel().attr(Attr_Message_Length).set(len);
                    } else {
                        return; // 等待更多数据
                    }
                }
                // 看来数据够了,读取吧
                if (in.isReadable(len)) {
                    // Ngrok消息的主体是一个JSON字符串
                    out.add(NgrokAgent.readMsg(in.toString(in.readerIndex(), len, Encoding.CHARSET_UTF8)));
                    in.readBytes(len); // 需要把数据消耗掉, 因为 ByteBuf.toString并不
                    ctx.channel().attr(Attr_Message_Length).set(null); // 重置长度, 准备读取下一条消息
                    return; // 继续下一条消息
                }
            }
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.debug("bad ngrok message?", cause);
            ctx.close();
        }

    }

    /**
     * Ngrok消息编码器
     * @author wendal
     *
     */
    class NgrokMessageEncoder extends MessageToByteEncoder<NgrokMsg> {

        protected void encode(ChannelHandlerContext ctx, NgrokMsg msg, ByteBuf out)
                throws Exception {
            NgrokAgent.writeMsg(new ByteBufOutputStream(out), msg);
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.debug("encode ngrok message fail?", cause);
            ctx.close();
        }
    }

    /**
     * Ngrok业务处理器
     * @author wendal
     *
     */
    class NgrokContrlHandler extends ChannelInboundHandlerAdapter {

        String id; // 自身Id, 也是ClientId

        boolean authed; // 避免重复auth的标志位

        boolean gzip_proxy; // 是否支持GzipProxy压缩, 预留

        NgrokMsg authMsg; // 客户端的登录消息

        long lastPing; // 最后心跳时间

        boolean proxyMode; // 是否处于Proxy代理链接状态

        ChannelHandlerContext ctx; // 当前链接的上下文

        public ArrayBlockingQueue<ChannelHandlerContext> idleProxys;
        public ArrayBlockingQueue<NgrokHttpHandler> waitProxys;

        public void channelRead(ChannelHandlerContext ctx, Object _msg) throws Exception {
            this.ctx = ctx;
            NgrokMsg msg = (NgrokMsg) _msg;
            String type = msg.getType();
            if (debug)
                log.debug("msg type = " + type);
            if ("Auth".equals(type)) {
                if (authed) { // 是不是没必要的?
                    ctx.writeAndFlush(NgrokMsg.authResp("", "Auth Again?!!"));
                    ctx.close();
                    return;
                }
                // 鉴权,取决于具体实现了
                if (!auth.check(NgrokNettyServer.this, msg)) {
                    ctx.writeAndFlush(NgrokMsg.authResp("", "AuthError"));
                    ctx.close();
                    return;
                }
                // 登录成功, 把两个队列准备好, 分别缓存通往客户端和浏览器的链接
                idleProxys = new ArrayBlockingQueue<ChannelHandlerContext>(128);
                waitProxys = new ArrayBlockingQueue<NgrokHttpHandler>(128);
                // 若客户端指定了id,那就给它吧
                id = msg.getString("ClientId");
                if (Strings.isBlank(id))
                    id = R.UU32(); // 否则,生成一个新的
                // 预留压缩支持
                gzip_proxy = msg.getBoolean("GzipProxy", false);
                if (log.isDebugEnabled())
                    log.debugf("New Client >> id=%s gzip_proxy=%s", id, gzip_proxy);
                // 告诉客户端, 登录成功了
                ctx.writeAndFlush(NgrokMsg.authResp(id, ""));
                msg.put("ClientId", id); // TODO 已经存在老的客户端呢? 直接替换是不是太暴力了?
                authMsg = msg; // 存好
                authed = true; // 标记登录成功
                lastPing = System.currentTimeMillis(); // 记录一下最后心跳时间
                clientHanlders.put(id, this);
            } else if ("ReqTunnel".equals(type)) {
                // 未登录就想创建连接,妄想...
                if (!authed) {
                    ctx.writeAndFlush(NgrokMsg.newTunnel("", "", "", "Not Auth Yet"));
                    ctx.close();
                    return;
                }
                // 看看auth实现给你多少个映射地址吧
                String[] mapping = auth.mapping(NgrokNettyServer.this, id, authMsg, msg);
                if (mapping == null || mapping.length == 0) {
                    ctx.writeAndFlush(NgrokMsg.newTunnel("", "", "", "pls check your token"));
                    ctx.close();
                    return;
                }
                // 需要把客户端提供的ReqId与映射地址一一对应起来
                String reqId = msg.getString("ReqId");
                for (String host : mapping) {
                    // 逐个下发通知,并要求上来一个Proxy链接
                    ctx.writeAndFlush(NgrokMsg.newTunnel(reqId, "http://" + host, "http", ""));
                    ctx.writeAndFlush(NgrokMsg.reqProxy(reqId, "http://" + host, "http", ""));
                    hostmap.put(host, id); // 冲突了怎么办?
                    reqIdMap.put(host, reqId);
                }
            } else if ("Ping".equals(type)) {
                // Ping --> Pong, 心跳嘛
                ctx.writeAndFlush(NgrokMsg.pong());
                lastPing = System.currentTimeMillis();
            } else if ("Pong".equals(type)) {
                // 客户端响应Pong
                lastPing = System.currentTimeMillis();
            } else if ("RegProxy".equals(type)) {
                // 客户端说要登记一个Proxy链接
                String clientId = msg.getString("ClientId");
                // 从已知客户端中找一下
                NgrokContrlHandler client = clientHanlders.get(clientId);
                if (client == null) {
                    log.debug("not such client id=" + clientId);
                    ctx.close();
                    return;
                }
                // 标记为代理链接
                proxyMode = true;
                // 看看有没有Http请求在等待
                NgrokHttpHandler httpHandler = client.waitProxys.poll();
                if (httpHandler == null) {
                    // 没有? 那就缓存着
                    log.debug("没有正在等待NgrokHttpHandler, 把Proxy链接加入到队列. CliendId=" + client.id);
                    client.idleProxys.add(ctx);
                }
                else {
                    // 有, 立马同步,写入数据,建立隧道
                    log.debug("有NgrokHttpHandler正在等待,启动代理线程. CliendId=" + client.id);
                    synchronized (httpHandler.lock) {
                        httpHandler.proxy = ctx;
                        httpHandler.wait = false;
                        startProxy(httpHandler.ctx,
                                   ctx,
                                   httpHandler.host,
                                   httpHandler.bao.toByteArray(),
                                   null);
                        httpHandler.bao = null; // 清理数据
                    }
                }
            } else {
                // 不认识的,就忽略吧
                log.info("未知消息类型 Type=" + type);
            }
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.debug("handle ngrok message fail?", cause);
            ctx.close();
        }
    }

    class NgrokHttpHandler extends ByteToMessageDecoder {

        ByteArrayOutputStream bao = new ByteArrayOutputStream();

        ChannelHandlerContext proxy;
        ChannelHandlerContext ctx;
        boolean wait;
        String host;
        Object lock = new Object();

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
                throws Exception {
            // 正在等Proxy链接吗?
            if (wait) { // 是的, 立马同步之
                synchronized (lock) {
                    if (wait) { // 还在等? 那把数据缓存一下吧
                        log.debug("有数据传入,但Proxy链接尚未建立,将数据放入缓存");
                        Streams.write(bao, new ByteBufInputStream(in));
                        return;
                    }
                }
            }
            // 没在等,看看Proxy链接是不是已经建立了?
            if (proxy != null) {
                // 是的啊, 那就把写数据到隧道啦
                log.debug("Proxy链接已建立,桥接数据");
                proxy.writeAndFlush(in);
                in.retain();
                return;
            }

            while (true) {
                // 下面一堆逻辑,就是找到一个Host值
                int index = in.bytesBefore((byte) '\n'); // 有无分行
                if (index == -1) { // 木有,那就再等
                    // 读了8k的头部还找不到Host,去死吧...
                    if (in.readableBytes() + bao.size() > 8192) {
                        ctx.close();
                    }
                    // 继续等
                    return;
                }
                if (index < 3) { // \r\n
                    ctx.close(); // 读取Header已经结束,但host没找到? 干掉你.
                    return;
                }
                byte[] buf = new byte[index + 1]; // 把换行符也读取哦
                in.readBytes(buf);
                bao.write(buf); //数据缓存一下,谢谢
                if (index > 5) {
                    String headerLine = new String(buf).trim();
                    // log.info(">> " + headerLine);
                    if (headerLine.toLowerCase().startsWith("host:")
                        || headerLine.toLowerCase().startsWith("host ")) { // Host: 或者 Host : ,总能匹配一种吧
                        String[] tmp = headerLine.split(":"); // 顺便把端口号去掉了
                        String host = tmp[1].trim();
                        log.debug("Host : " + host); // 找到Host了,那就打印一下
                        String cliendId = hostmap.get(host.toLowerCase());
                        if (cliendId == null) {
                            // 木有ClientId, 干掉
                            log.debugf("Host[%s] without Ngrok Client Id", host);
                            ctx.writeAndFlush(ctx.channel()
                                                 .alloc()
                                                 .buffer()
                                                 .writeBytes("HTTP/1.0 404 Not Found\r\n\r\n".getBytes()));
                            ctx.close();
                            return;
                        }
                        // 看看控制器在线不?
                        NgrokContrlHandler handler = clientHanlders.get(cliendId);
                        if (handler == null) {
                            // 木有,404侍候
                            log.debugf("Host[%s] without Ngrok Client", host);
                            ctx.writeAndFlush(ctx.channel()
                                                 .alloc()
                                                 .buffer()
                                                 .writeBytes("HTTP/1.0 404 Not Found\r\n\r\n".getBytes()));
                            ctx.close();
                            return;
                        }
                        // 看看有无已经待命的Proxy链接
                        proxy = handler.idleProxys.poll();
                        if (proxy == null) {
                            // 木有,好惨,那看看有无对应的ReqId吧
                            String reqId = reqIdMap.get(host);
                            if (reqId == null) {
                                // 依然木有,干掉你, 但不应该出现的
                                log.debug("没有合适的RequestId, 关闭链接");
                                ctx.close();
                                return;
                            }
                            // 等吧...
                            wait = true;
                            this.ctx = ctx;
                            this.host = host;
                            log.debug("没有可用的Proxy链接,将自身加入到waitProxys, handler.id=" + handler.id);
                            handler.waitProxys.add(this);
                            // 要客户端给Proxy链接!!
                            handler.ctx.writeAndFlush(NgrokMsg.reqProxy(reqId,
                                                                        "http://" + host,
                                                                        "http",
                                                                        ""));
                            return;
                        } else {
                            // 很好很强大
                            log.debug("有可用Proxy链接, 开始启用Proxy代理链路");
                            startProxy(ctx, proxy, host, bao.toByteArray(), in);
                            bao = null;
                            return;
                        }
                    }
                }
            }
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            log.debug("handle http message fail?", cause);
            ctx.close();
        }

    }

    /**
     * A有数据输入,就写入到B,变成一个管道
     * @author wendal
     *
     */
    class Piped extends ChannelInboundHandlerAdapter {

        protected ChannelHandlerContext target;

        public Piped(ChannelHandlerContext target) {
            this.target = target;
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            target.writeAndFlush(msg);
            ((ByteBuf)msg).retain();
        }

        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.debug("close?", cause);
            target.close();
            ctx.close();
        }
        
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            log.debug("ChannelHandlerContext close?"); // 其中一端关闭,那就全部关闭
            target.close();
            ctx.close();
        }
    }

    public void startProxy(final ChannelHandlerContext ctx,
                           ChannelHandlerContext proxy,
                           String host,
                           byte[] buf,
                           ByteBuf in) {
        // 给Proxy链接发通知
        proxy.write(NgrokMsg.startProxy("http://" + host, ""));
        proxy.pipeline().remove("ngrok.decode"); // 不再需要ngrok解码器
        proxy.pipeline().remove("ngrok.handler"); // 不再需要ngrok处理器
        proxy.pipeline().addLast(new Piped(ctx)); // 加入管道处理器
        if (buf != null) // 把预先读取的数据写进去
            proxy.write(proxy.alloc().buffer().writeBytes(buf));
        if (in != null) { // 把剩余的数据也写进入
            proxy.write(in);
            in.retain();
        }
        // 刷新出去,不然还得等缓存区满,那是挂的节奏
        proxy.flush();
    }

    public static void main(String[] args) throws Exception {
        NgrokNettyServer server = new NgrokNettyServer();
        if (!NgrokAgent.fixFromArgs(server, args)) {
            log.debug("usage : -srv_host=wendal.cn -srv_port=4443 -http_port=9080 -ssl_jks_path=wendal.cn.jks -ssl_jks_password=123456 -conf_file=xxx.properties");
        }
        server.start();
    }
}
