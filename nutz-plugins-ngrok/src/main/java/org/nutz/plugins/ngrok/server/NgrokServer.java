package org.nutz.plugins.ngrok.server;

import java.net.ServerSocket;
import java.util.concurrent.Callable;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class NgrokServer implements Callable<Object> {

    SSLServerSocket serverSocket;
    SSLServerSocketFactory sslServerSocketFactory;
    
    @Override
    public Object call() throws Exception {
        if (sslServerSocketFactory == null)
            sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket();
//        serverSocket.
        return null;
    }
}
