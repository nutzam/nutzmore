package org.nutz.integration.autoloadcache;

import com.jarvis.cache.CacheHandler;
import com.jarvis.cache.annotation.CacheDeleteTransactional;
import com.jarvis.cache.aop.DeleteCacheTransactionalAopProxyChain;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;

import java.lang.reflect.Method;

public class TxDelCacheAopInterceptor implements MethodInterceptor {

    private CacheHandler cacheHandler;

    private CacheDeleteTransactional cache;

    private boolean haveCache;

    public TxDelCacheAopInterceptor(CacheHandler cacheHandler, CacheDeleteTransactional cache, Method method) {
        this.cacheHandler = cacheHandler;
        this.cache = cache;
        if (method.isAnnotationPresent(CacheDeleteTransactional.class)) {
            this.haveCache = true;
        }
    }


    @Override
    public void filter(final InterceptorChain chain) throws Throwable {
        try {
            if (haveCache) {
                cacheHandler.proceedDeleteCacheTransactional(new DeleteCacheTransactionalAopProxyChain() {
                    @Override
                    public Object doProxyChain() throws Throwable {
                        return chain.doChain();
                    }
                }, cache);
            } else {
                chain.doChain();
            }
        } catch (Throwable e) {
            throw e;
        }
    }
}
