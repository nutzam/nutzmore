package org.nutz.http.sender.jetty;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.HttpCookieStore;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.nutz.http.Request;
import org.nutz.http.Sender;
import org.nutz.http.SenderFactory;
import org.nutz.ioc.impl.PropertiesProxy;

public class JettySenderFactory implements SenderFactory, AutoCloseable {
    
    protected static final String PRE = "jetty.http.client.";
    
    protected HttpClient client;
    
    protected QueuedThreadPool executor;
    
    public JettySenderFactory() throws Exception {
        this(new PropertiesProxy());
    }
    
    public JettySenderFactory(PropertiesProxy conf) throws Exception {
        client = new HttpClient(new SslContextFactory(true));
        client.setFollowRedirects(false);
        client.setCookieStore(new HttpCookieStore.Empty());

        executor = new QueuedThreadPool(conf.getInt(PRE + ".maxThreads", 256));
        client.setExecutor(executor);
        client.setMaxConnectionsPerDestination(conf.getInt(PRE + ".maxConnections", 256));
        client.setIdleTimeout(conf.getLong(PRE + ".idleTimeout", 30000));

        client.setConnectTimeout(conf.getLong(PRE + ".connectTime", 1000));

        if (conf.has(PRE + "requestBufferSize"))
            client.setRequestBufferSize(conf.getInt(PRE + "requestBufferSize"));

        if (conf.has(PRE + "responseBufferSize"))
            client.setResponseBufferSize(conf.getInt(PRE + "responseBufferSize"));

        client.start();
    }
    
    public JettySenderFactory(HttpClient client) throws Exception {
        this.client = client;
        if (!client.isStarted())
            client.start();
    }

    public Sender create(Request request) {
        return new JettySender(client, request);
    }

    public void close() throws Exception {
        if (client != null && client.isStarted()) {
            client.stop();
        }
    }
}
