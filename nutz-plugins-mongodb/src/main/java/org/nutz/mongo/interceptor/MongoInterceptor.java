package org.nutz.mongo.interceptor;

public interface MongoInterceptor {

    void filter(MongoInterceptorChain<?> chain);
}
