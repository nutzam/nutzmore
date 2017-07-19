package org.nutz.plugins.ngrok.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.ngrok.server.auth.DefaultNgrokAuthProvider;
import org.nutz.plugins.ngrok.server.auth.NgrokAuthProvider;
import org.nutz.plugins.ngrok.server.auth.SimpleRedisAuthProvider;

public abstract class AbstractNgrokServer {
    
    private static final Log log = Logs.get();
    public Map<String, String> hostmap = new ConcurrentHashMap<String, String>();
    public Map<String, String> reqIdMap = new ConcurrentHashMap<String, String>();
    public byte[] ssl_jks;
    public NgrokAuthProvider auth;
    
    public PropertiesProxy conf;
    public String ssl_jks_password = "123456";
    public String ssl_jks_path;
    public int srv_port = 4443;
    public int http_port = 9080;
    public int status;
    public String srv_host = "wendal.cn";
    public int client_proxy_init_size = 1;
    public int client_proxy_wait_timeout = 30 * 1000;
    public int bufSize = 8192;
    public boolean redis;
    public String redis_host = "127.0.0.1";
    public int redis_port = 6379;
    public String redis_key = "ngrok";
    public String redis_rkey;
    public boolean debug;
    
    public void init() {
        if (auth == null) {
            if (redis) {
                log.debug("using redis auth provider");
                auth = new SimpleRedisAuthProvider(redis_host, redis_port, redis_key);
            } else {
                log.debug("using default ngrok auth provider");
                auth = new DefaultNgrokAuthProvider();
            }
        } else {
            log.debug("using custom auth provider class=" + auth.getClass().getName());
        }
    }
    


    public SSLServerSocketFactory buildSSL() throws Exception {
        return buildSSLContext().getServerSocketFactory();
    }
    
    public SSLContext buildSSLContext() throws Exception {
        log.debug("try to load Java KeyStore File ...");
        KeyStore ks = KeyStore.getInstance("JKS");
        if (ssl_jks != null)
            ks.load(new ByteArrayInputStream(ssl_jks), ssl_jks_password.toCharArray());
        else if (ssl_jks_path != null) {
            log.debug("load jks from " + this.ssl_jks_path);
            ks.load(new FileInputStream(this.ssl_jks_path), ssl_jks_password.toCharArray());
        } else if (new File(srv_host + ".jks").exists()) {
            log.debug("load jks from " + srv_host + ".jks");
            ks.load(new FileInputStream(srv_host + ".jks"), ssl_jks_password.toCharArray());
        } else
            throw new RuntimeException("must set ssl_jks_path or ssl_jks");

        TrustManagerFactory tmfactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmfactory.init(ks);
        TrustManager[] tms = tmfactory.getTrustManagers();

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, ssl_jks_password.toCharArray());

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(kmf.getKeyManagers(), tms, new SecureRandom());

        return sc;
    }
    
    public abstract void start() throws Exception;
}
