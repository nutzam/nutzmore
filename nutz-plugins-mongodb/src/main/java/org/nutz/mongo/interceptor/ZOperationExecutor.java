package org.nutz.mongo.interceptor;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.ReadPreference;
import com.mongodb.operation.OperationExecutor;
import com.mongodb.operation.ReadOperation;
import com.mongodb.operation.WriteOperation;

public class ZOperationExecutor implements OperationExecutor {
    
    protected OperationExecutor proxy;
    protected List<MongoInterceptor> interceptors;
    
    protected ZOperationExecutor() {}

    public ZOperationExecutor(OperationExecutor proxy, List<MongoInterceptor> interceptors) {
        this.proxy = proxy;
        this.interceptors = interceptors;
    }



    public <T> T execute(ReadOperation<T> operation, ReadPreference readPreference) {
        if (interceptors == null)
            return proxy.execute(operation, readPreference);
        else {
            MongoInterceptorChain<T> chain = new MongoInterceptorChain<T>();
            chain.interceptors = new ArrayList<MongoInterceptor>(interceptors);
            chain.proxy = proxy;
            chain.readOperation = operation;
            chain.readPreference = readPreference;
            chain.doChain();
            return chain.result;
        }
    }

    public <T> T execute(WriteOperation<T> operation) {
        if (interceptors == null)
            return proxy.execute(operation);
        else {
            MongoInterceptorChain<T> chain = new MongoInterceptorChain<T>();
            chain.interceptors = new ArrayList<MongoInterceptor>(interceptors);
            chain.proxy = proxy;
            chain.writeOperation = operation;
            chain.doChain();
            return chain.result;
        }
    }

}
