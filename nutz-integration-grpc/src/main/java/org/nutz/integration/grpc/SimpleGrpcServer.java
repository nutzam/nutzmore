
package org.nutz.integration.grpc;

import java.io.File;
import java.io.IOException;

import org.nutz.log.Log;
import org.nutz.log.Logs;

import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * 简单封装一下
 */
public class SimpleGrpcServer {

    private static final Log log = Logs.get();

    private Server server;

    protected int port = 50051;
    
    protected DynamicHandlerRegistry registry;
    
    protected File certChain;
    
    protected File privateKey;

    public void start() {
        try {
            if (registry == null)
                registry = new DynamicHandlerRegistry();
            ServerBuilder<?> sb = ServerBuilder.forPort(port);
            if (certChain != null)
                sb.useTransportSecurity(certChain, privateKey);
            server = sb.fallbackHandlerRegistry(registry).build().start();
            log.info("gRPC Server started, listening on " + port);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public void setRegistry(DynamicHandlerRegistry registry) {
        this.registry = registry;
    }
    
    public DynamicHandlerRegistry getRegistry() {
        return registry;
    }
    
    public Server getServer() {
        return server;
    }
    
    public int getPort() {
        return port;
    }
}