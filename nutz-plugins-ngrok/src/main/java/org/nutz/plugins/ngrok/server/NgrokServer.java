package org.nutz.plugins.ngrok.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.nutz.lang.Files;
import org.nutz.lang.Streams;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.ngrok.common.NgrokAgent;
import org.nutz.plugins.ngrok.common.NgrokAuthProvider;
import org.nutz.plugins.ngrok.common.NgrokMsg;
import org.nutz.plugins.ngrok.common.StatusProvider;

public class NgrokServer implements Callable<Object>, StatusProvider<Integer> {
    
    private static final Log log = Logs.get();

    SSLServerSocket serverSocket;
    public SSLServerSocketFactory sslServerSocketFactory;
    public char[] ssl_jks_password;
    public byte[] ssl_jks;
    public int ctl_port;
    public int http_port;
    public ExecutorService executorService;
    public int status;
    public Map<String, NgrokServerClient> clients = new ConcurrentHashMap<String, NgrokServerClient>();
    public NgrokAuthProvider auth;
    public String hostname;
    
    public void start() throws Exception {
        if (sslServerSocketFactory == null)
            sslServerSocketFactory = buildSSL();
        if (executorService == null)
            executorService = Executors.newCachedThreadPool();
        status = 1;
        executorService.submit(this);
    }
    
    public void stop() {
        status = 3;
        executorService.shutdown();
    }
    
    @Override
    public Object call() throws Exception {
        serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(ctl_port);
        while (status == 1) {
            Socket socket = serverSocket.accept();
            executorService.submit(new ClientConnThread(socket));
        }
        return null;
    }
    
    public SSLServerSocketFactory buildSSL() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new ByteArrayInputStream(ssl_jks), ssl_jks_password);
        
        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(ks);
        TrustManager[] tms = tmfactory.getTrustManagers();
        
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, ssl_jks_password);

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(kmf.getKeyManagers(), tms, new SecureRandom());
        
        return sc.getServerSocketFactory();
    }
    
    public class ClientConnThread implements Callable<Object> {
        
        protected Socket socket;
        protected InputStream ins;
        protected OutputStream out;
        protected boolean proxyMode;
        protected boolean authed;
        protected String id;
        public Set<Socket> proxySockets = new HashSet<Socket>();
        public NgrokMsg authMsg;
        public long lastPing;
        
        public ClientConnThread(Socket socket) {
            this.socket = socket;
        }

        public Object call() throws Exception {
            try {
                this.ins = socket.getInputStream();
                this.out = socket.getOutputStream();
                while (true) {
                    NgrokMsg msg = NgrokAgent.readMsg(ins);
                    String type = msg.getString("Type");
                    if ("Auth".equals(type)) {
                        if (authed) {
                            NgrokAgent.writeMsg(out, NgrokMsg.authResp("", "Auth Again?!!"));
                            break;
                        }
                        if (auth != null && !auth.check(msg)) {
                            NgrokAgent.writeMsg(out, NgrokMsg.authResp("", "AuthError"));
                            break;
                        }
                        id = R.UU32();
                        if (log.isDebugEnabled())
                            log.debug("New Client >> id=" +id);
                        NgrokAgent.writeMsg(out, NgrokMsg.authResp(id, ""));
                        authMsg = msg;
                        authed = true;
                        lastPing = System.currentTimeMillis();
                    } else if ("ReqTunnel".equals(type)) {
                        if (!authed) {
                            NgrokAgent.writeMsg(out, NgrokMsg.newTunnel("", "", "Not Auth Yet"));
                            break;
                        }
                        String[] mapping;
                        if (auth == null) {
                            mapping = new String[]{id};
                        } else {
                            mapping = auth.mapping(authMsg, msg);
                        }
                        if (mapping == null || mapping.length == 0) {
                            NgrokAgent.writeMsg(out, NgrokMsg.newTunnel("", "", "Not Channel To Give"));
                            break;
                        }
                        String reqId = msg.getString("ReqId");
                        for (String mapp : mapping) {
                            String url = "http://"+mapp + "." + hostname;
                            NgrokAgent.writeMsg(out, NgrokMsg.newTunnel(reqId, url, ""));
                        }
                    } else if ("Ping".equals(type)) {
                        NgrokAgent.writeMsg(out, NgrokMsg.pong());
                    } else if ("Pong".equals(type)) {
                        lastPing = System.currentTimeMillis();
                    } else if ("RegProxy".equals(type)){
                        String clientId = msg.getString("ClientId");
                        NgrokServerClient client = clients.get(clientId);
                        if (client == null) {
                            log.debug("not such client id=" + clientId);
                            break;
                        }
                        client.proxySockets.add(socket);
                        proxyMode = true;
                        break;
                    } else {
                        log.info("Bad Type=" + type);
                        break;
                    }
                }
            } finally {
                if (!proxyMode)
                    Streams.safeClose(socket);
            }
            return null;
        }
        
    }
    
    @Override
    public Integer getStatus() {
        return status;
    }
    
    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.debug","all");
        
        NgrokServer server = new NgrokServer();
        server.ssl_jks = Files.readBytes("D:\\wendal.cn.jks");
        server.ssl_jks_password = "123456".toCharArray();
        server.hostname = "wendal.cn";
        server.ctl_port = 4443;
        server.auth = new NgrokAuthProvider() {
            public String[] mapping(NgrokMsg auth, NgrokMsg req) {
                return new String[]{"wendal.ngrok"};
            }
            
            public boolean check(NgrokMsg auth) {
                return true;
            }
        };
        server.start();
    }
    // 使用 crt和key文件, 也就是nginx使用的证书,生成jks的步骤
    // 首先, 使用openssl生成p12文件,必须输入密码
    // openssl pkcs12 -export -in 1_wendal.cn_bundle.crt -inkey 2_wendal.cn.key -out wendal.cn.p12
    // 然后, 使用keytool 生成jks
    // keytool -importkeystore -destkeystore wendal.cn.jks -srckeystore wendal.cn.p12 -srcstoretype pkcs12 -alias 1
}
