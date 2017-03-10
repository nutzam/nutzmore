package org.nutz.plugins.thrift.netty.common.pool;

import java.io.Closeable;

import org.apache.commons.pool2.impl.GenericObjectPool;

/**
 * @author rekoe
 *
 */
public abstract class Pool<T> implements Closeable {

    protected GenericObjectPool<T> internalPool;

    public T getResource() {
        try {
            return this.internalPool.borrowObject();
        } catch (Exception e) {
            throw new ConnectionException("Could not get a resource from the pool", e);
        }
    }

    public void returnResource(T resource) {
        if (resource != null) {
            try {
                this.internalPool.returnObject(resource);
            } catch (Exception e) {
                throw new PoolException("Could not return the resource to the pool", e);
            }
        }
    }
    
    public void returnBrokenResource(T resource) {
        if (resource != null) {
            try {
                this.internalPool.invalidateObject(resource);
            } catch (Exception e) {
                throw new PoolException("Could not return the resource to the pool", e);
            }
        }
    }
    
    @Override
    public void close() {
        try {
            this.internalPool.close();
        } catch (Exception e) {
            throw new PoolException("Could not destroy the pool", e);
        }
    }
}
