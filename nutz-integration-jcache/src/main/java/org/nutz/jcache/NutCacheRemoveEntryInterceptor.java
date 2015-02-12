package org.nutz.jcache;

import org.jsr107.ri.annotations.AbstractCacheRemoveEntryInterceptor;
import org.jsr107.ri.annotations.CacheContextSource;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public class NutCacheRemoveEntryInterceptor extends AbstractCacheRemoveEntryInterceptor<InterceptorChain> implements MethodInterceptor {

    @Inject
    protected CacheContextSource<InterceptorChain> cacheContextSource;
    
    protected Object proceed(InterceptorChain invocation) throws Throwable {
        return invocation.doChain().getReturn();
    }

    public void filter(InterceptorChain chain) throws Throwable {
        Object obj = cacheRemoveEntry(cacheContextSource, chain);
        chain.setReturnValue(obj);
    }
}
