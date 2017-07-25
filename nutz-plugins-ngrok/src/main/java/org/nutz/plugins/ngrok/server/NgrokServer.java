package org.nutz.plugins.ngrok.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.ngrok.common.NgrokAgent;
import org.nutz.plugins.ngrok.common.NgrokMsg;
import org.nutz.plugins.ngrok.common.PipedStreamThread;
import org.nutz.plugins.ngrok.server.NgrokServer.NgrokServerClient.ProxySocket;

public class NgrokServer extends AbstractNgrokServer implements Callable<Object> {

    private static final Log log = Logs.get();

    public transient SSLServerSocket mainCtlSS;
    public transient ServerSocket httpSS;
    public transient SSLServerSocketFactory sslServerSocketFactory;
    
    public Map<String, NgrokServerClient> clients = new ConcurrentHashMap<String, NgrokServerClient>();
    public ExecutorService executorService;

    public void start() throws Exception {
        log.debug("NgrokServer start ...");
        if (sslServerSocketFactory == null)
            sslServerSocketFactory = buildSSL();
        if (executorService == null) {
            log.debug("using default CachedThreadPool");
            executorService = Executors.newCachedThreadPool();
        }
        init();
        status = 1;

        // 先创建监听,然后再启动哦,
        log.debug("start listen srv_port=" + srv_port);
        mainCtlSS = (SSLServerSocket) sslServerSocketFactory.createServerSocket(srv_port);
        log.debug("start listen http_port=" + http_port);
        httpSS = new ServerSocket(http_port);

        log.debug("start Contrl Thread...");
        executorService.submit(this);
        log.debug("start Http Thread...");
        executorService.submit(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                while (status == 1) {
                    Socket socket = httpSS.accept();
                    executorService.submit(new HttpThread(socket));
                }
                return null;
            }
        });
    }

    public void stop() {
        status = 3;
        executorService.shutdown();
    }

    @Override
    public Object call() throws Exception {
        while (status == 1) {
            try {
                executorService.submit(new NgrokServerClient(mainCtlSS.accept()));
            }
            catch (Throwable e) {
                log.warn("something happen", e);
                Lang.quiteSleep(1000);
            }
        }
        return null;
    }

    public class NgrokServerClient implements Callable<Object> {

        protected Socket socket;
        protected InputStream ins;
        protected OutputStream out;
        protected boolean proxyMode;
        protected boolean authed;
        public String id;
        public ArrayBlockingQueue<ProxySocket> idleProxys = new ArrayBlockingQueue<NgrokServer.NgrokServerClient.ProxySocket>(128);
        public NgrokMsg authMsg;
        public long lastPing;
        public boolean gzip_proxy;

        public NgrokServerClient(Socket socket) {
            this.socket = socket;
        }

        public Object call() throws Exception {
            try {
                this.socket.setSoTimeout(2*60*1000);
                this.ins = socket.getInputStream();
                this.out = socket.getOutputStream();
                while (true) {
                    NgrokMsg msg = NgrokAgent.readMsg(ins);
                    String type = msg.getType();
                    if ("Auth".equals(type)) {
                        if (authed) {
                            NgrokMsg.authResp("", "Auth Again?!!").write(out);
                            break;
                        }
                        if (!auth.check(NgrokServer.this, msg)) {
                            NgrokMsg.authResp("", "AuthError").write(out);
                            break;
                        }
                        id = msg.getString("ClientId");
                        if (Strings.isBlank(id))
                            id = R.UU32();
                        gzip_proxy = msg.getBoolean("GzipProxy", false);
                        if (log.isDebugEnabled())
                            log.debugf("New Client >> id=%s gzip_proxy=%s", id, gzip_proxy);
                        NgrokMsg.authResp(id, "").write(out);
                        msg.put("ClientId", id);
                        authMsg = msg;
                        authed = true;
                        lastPing = System.currentTimeMillis();
                        clients.put(id, this);
                    } else if ("ReqTunnel".equals(type)) {
                        if (!authed) {
                            NgrokMsg.newTunnel("", "", "", "Not Auth Yet").write(out);
                            break;
                        }
                        String[] mapping = auth.mapping(NgrokServer.this,
                                                        id, authMsg,
                                                        msg);
                        if (mapping == null || mapping.length == 0) {
                            NgrokMsg.newTunnel("", "", "", "pls check your token").write(out);
                            break;
                        }
                        String reqId = msg.getString("ReqId");
                        for (String host : mapping) {

                            NgrokMsg.newTunnel(reqId, "http://" + host, "http", "").write(out);
                            hostmap.put(host, id); // 冲突了怎么办?
                            reqIdMap.put(host, reqId);
//                            reqProxy(host);
                        }
                    } else if ("Ping".equals(type)) {
                        NgrokMsg.pong().write(out);
                    } else if ("Pong".equals(type)) {
                        lastPing = System.currentTimeMillis();
                    } else if ("RegProxy".equals(type)) {
                        String clientId = msg.getString("ClientId");
                        NgrokServerClient client = clients.get(clientId);
                        if (client == null) {
                            log.debug("not such client id=" + clientId);
                            break;
                        }
                        this.socket.setSoTimeout(3600*1000);
                        proxyMode = true;
                        ProxySocket proxySocket = new ProxySocket(socket);
                        client.idleProxys.add(proxySocket);
                        break;
                    } else {
                        log.info("Bad Type=" + type);
                        break;
                    }
                }
            }
            catch (Throwable e) {
                log.info("something happen!!!", e);
            }
            finally {
                if (!proxyMode) {
                    Streams.safeClose(socket);
                    clean();
                }
            }
            return null;
        }

        public boolean reqProxy(String host) throws IOException {
            String reqId = reqIdMap.get(host);
            if (reqId == null)
                return false;
            for (int i = 0; i < 5; i++) {
                NgrokAgent.writeMsg(out, NgrokMsg.reqProxy(reqId, "http://" + host, "http", ""));
            }
            return true;
        }

        public class ProxySocket {
            public Socket socket;
            public long createAt;

            public ProxySocket(Socket socket) {
                this.socket = socket;
                createAt = System.currentTimeMillis();
            }
            protected void finalize() throws Throwable {
                Streams.safeClose(socket);
            }
        }

        public ProxySocket getProxy(String host) throws Exception {
            ProxySocket ps = null;
            while (true) {
                ps = idleProxys.poll();
                if (ps == null)
                    break;
                if (System.currentTimeMillis() - ps.createAt > 65*1000) {
                    Streams.safeClose(ps.socket);
                    continue;
                }
                try {
                    NgrokAgent.writeMsg(ps.socket.getOutputStream(), NgrokMsg.startProxy("http://" + host, ""));
                    return ps;
                } catch (Exception e) {
                    continue;
                }
            }
            if (ps == null) {
                if (log.isDebugEnabled())
                    log.debugf("req proxy conn for host[%s]", host);
                if (reqProxy(host)) {
                    ps = idleProxys.poll(client_proxy_wait_timeout, TimeUnit.MILLISECONDS);
                    if (ps != null) {
                        NgrokAgent.writeMsg(ps.socket.getOutputStream(), NgrokMsg.startProxy("http://" + host, ""));
                        return ps;
                    }
                }
            }
            return ps;
        }

        public void clean() {
            clients.remove(id);
            while (true) {
                ProxySocket proxySocket = idleProxys.poll();
                if (proxySocket != null)
                    Streams.safeClose(proxySocket.socket);
                else
                    break;
            }
        }

        public boolean isRunning() {
            return socket != null && socket.isConnected();
        }

    }

    public class HttpThread implements Callable<Object> {
        public Socket socket;
        InputStream _ins;
        OutputStream _out;

        public HttpThread(Socket socket) {
            super();
            this.socket = socket;
        }

        public Object call() throws Exception {

            _ins = socket.getInputStream();
            _out = socket.getOutputStream();

            HttpBuf httpBuf = new HttpBuf();
            try {
                int line_start = 0;
                byte[] buf = new byte[1];
                List<String> lines = new ArrayList<String>();
                while (true) {
                    int len = _ins.read(buf);
                    if (len == -1)
                        break;
                    else if (len == 0)
                        continue;
                    if (httpBuf.size() > 8192) {
                        NgrokAgent.httpResp(_out,
                                            400,
                                            "无法读取合法的Host,拒绝访问.不允许ip直接访问,同时Host必须存在于请求的前8192个字节!");
                        return null;
                    }
                    httpBuf.write(buf, 0, 1);
                    if (buf[0] == '\n') {
                        int size = httpBuf.size();
                        if (size - line_start <= 2) {
                            break;
                        }
                        String line = new String(httpBuf.getBuf(),
                                                 line_start,
                                                 size - 2 - line_start);
                        // log.debug(">> " + line);
                        lines.add(line);
                        line_start = size;
                    }
                }
                buf = httpBuf.toByteArray();
                httpBuf.close();
                httpBuf = null;
                if (lines.size() > 1) {
                    String firstLine = lines.remove(0);
                    for (String line : lines) {
                        if (line.toLowerCase().startsWith("host") && line.contains(":")) {
                            String key = line.substring(0, line.indexOf(':')).trim().toLowerCase();
                            if (!key.equals("host")) {
                                continue;
                            }
                            String host = line.substring(line.indexOf(':') + 1)
                                              .trim()
                                              .toLowerCase();
                            log.debugf("Host[%s] %s", host, firstLine);
                            if (host.contains(":"))
                                host = host.substring(0, host.indexOf(':'));
                            abcdefg(host.toLowerCase(), buf);
                            return null;
                        }
                    }
                }
            }
            finally {
                Streams.safeClose(httpBuf);
                Streams.safeClose(socket);
            }
            return null;
        }

        protected void abcdefg(String host, byte[] buf) throws Exception {

            //Stopwatch sw = Stopwatch.begin();
            String clientId = hostmap.get(host);
            if (clientId == null) {
                NgrokAgent.httpResp(_out, 404, "Tunnel " + host + " not found");
                return;
            }
            NgrokServerClient client = clients.get(clientId);
            if (client == null) {
                NgrokAgent.httpResp(_out, 404, "Tunnel " + host + " is Closed");
                return;
            }
            ProxySocket proxySocket;
            try {
                proxySocket = client.getProxy(host);
            }
            catch (Exception e) {
                log.debug("Get ProxySocket FAIL host=" + host, e);
                NgrokAgent.httpResp(_out,
                                    500,
                                    "Tunnel " + host + " did't has any proxy conntion yet!!");
                return;
            }
            //sw.tag("After Get ProxySocket");
            PipedStreamThread srv2loc = null;
            PipedStreamThread loc2srv = null;
            //NgrokAgent.writeMsg(proxySocket.socket.getOutputStream(), NgrokMsg.startProxy("http://" + host, ""));
            //sw.tag("After Send Start Proxy");
            proxySocket.socket.getOutputStream().write(buf);
            // 服务器-->本地
            srv2loc = new PipedStreamThread("http2proxy",
                                            _ins,
                                            NgrokAgent.gzip_out(client.gzip_proxy,
                                                                proxySocket.socket.getOutputStream()),
                                            bufSize);
            // 本地-->服务器
            loc2srv = new PipedStreamThread("proxy2http",
                                            NgrokAgent.gzip_in(client.gzip_proxy,
                                                               proxySocket.socket.getInputStream()),
                                            _out,
                                            bufSize);
            //sw.tag("After PipedStream Make");
            //sw.stop();
            //log.debug("ProxyConn Timeline = " + sw.toString());
            // 等待其中任意一个管道的关闭
            String exitFirst = executorService.invokeAny(Arrays.asList(srv2loc, loc2srv));
            if (log.isDebugEnabled())
                log.debug("proxy conn exit first at " + exitFirst);
        }

    }

    public static class HttpBuf extends ByteArrayOutputStream {
        public HttpBuf() {
            super(512);
        }

        public byte[] getBuf() {
            return buf;
        }
    }

    public static void main(String[] args) throws Exception {
        // System.setProperty("javax.net.debug","all");

        NgrokServer server = new NgrokServer();
        if (!NgrokAgent.fixFromArgs(server, args)) {
            log.debug("usage : -srv_host=wendal.cn -srv_port=4443 -http_port=9080 -ssl_jks_path=wendal.cn.jks -ssl_jks_password=123456 -conf_file=xxx.properties");
        }
        server.start();
    }
    // 使用 crt和key文件, 也就是nginx使用的证书,生成jks的步骤
    // 首先, 使用openssl生成p12文件,必须输入密码
    // openssl pkcs12 -export -in 1_wendal.cn_bundle.crt -inkey 2_wendal.cn.key
    // -out wendal.cn.p12
    // 然后, 使用keytool 生成jks
    // keytool -importkeystore -destkeystore wendal.cn.jks -srckeystore
    // wendal.cn.p12 -srcstoretype pkcs12 -alias 1
}
