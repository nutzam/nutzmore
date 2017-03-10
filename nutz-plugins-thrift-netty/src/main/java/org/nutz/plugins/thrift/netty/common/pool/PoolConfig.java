package org.nutz.plugins.thrift.netty.common.pool;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * @author rekoe
 * 
 */
public class PoolConfig {

    private GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
    
    public GenericObjectPoolConfig getPoolConfig() {
        return poolConfig;
    }

    public PoolConfig poolConfig(GenericObjectPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }
    
}
