package org.nutz.plugins.thrift.netty.client.pool;

import java.util.concurrent.TimeUnit;

import org.nutz.plugins.thrift.netty.common.pool.PoolConfig;

import io.airlift.units.Duration;

/**
 * @author rekoe
 * 
 */
public class ThriftClientConfig extends PoolConfig {

    private Duration connectTimeout = new Duration(1000, TimeUnit.MILLISECONDS);
    
    private Duration receiveTimeout = new Duration(1000, TimeUnit.MILLISECONDS);
    
    private Duration readTimeout = new Duration(1000, TimeUnit.MILLISECONDS);
    
    private Duration writeTimeout = new Duration(1000, TimeUnit.MILLISECONDS);
    
    public ThriftClientConfig connectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }
    
    public ThriftClientConfig receiveTimeout(Duration receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
        return this;
    }
    
    public ThriftClientConfig readTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }
    
    public ThriftClientConfig writeTimeout(Duration writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public Duration getReceiveTimeout() {
        return receiveTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public Duration getWriteTimeout() {
        return writeTimeout;
    }
    
}
