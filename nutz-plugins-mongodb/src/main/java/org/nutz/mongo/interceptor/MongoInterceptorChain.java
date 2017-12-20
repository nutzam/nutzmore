package org.nutz.mongo.interceptor;

import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.lang.util.Context;

import com.mongodb.ReadPreference;
import com.mongodb.operation.OperationExecutor;
import com.mongodb.operation.ReadOperation;
import com.mongodb.operation.WriteOperation;

public class MongoInterceptorChain<T> {

    protected ReadOperation<T> readOperation;
    protected ReadPreference readPreference;
    protected WriteOperation<T> writeOperation;
    protected List<MongoInterceptor> interceptors;
    protected int index;
    protected OperationExecutor proxy;
    protected T result;
    protected Context context;
    
    public void doChain() {
        if (interceptors.size() > index)
            interceptors.get(index++).filter(this);
        else {
            if (readOperation != null) {
                result = proxy.execute(readOperation, readPreference);
            } else if (writeOperation != null) {
                result = proxy.execute(writeOperation);
            }
        }
    }

    public ReadOperation<T> getReadOperation() {
        return readOperation;
    }

    public void setReadOperation(ReadOperation<T> readOperation) {
        this.readOperation = readOperation;
    }

    public ReadPreference getReadPreference() {
        return readPreference;
    }

    public void setReadPreference(ReadPreference readPreference) {
        this.readPreference = readPreference;
    }

    public WriteOperation<T> getWriteOperation() {
        return writeOperation;
    }

    public void setWriteOperation(WriteOperation<T> writeOperation) {
        this.writeOperation = writeOperation;
    }

    public List<MongoInterceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<MongoInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public OperationExecutor getProxy() {
        return proxy;
    }

    public void setProxy(OperationExecutor proxy) {
        this.proxy = proxy;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public Context getContext() {
        if (context == null)
            context = Lang.context();
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
