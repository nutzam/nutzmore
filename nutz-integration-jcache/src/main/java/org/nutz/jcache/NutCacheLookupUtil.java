package org.nutz.jcache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.annotation.CacheKeyGenerator;
import javax.cache.annotation.CacheResolverFactory;

import org.jsr107.ri.annotations.AbstractCacheLookupUtil;
import org.jsr107.ri.annotations.DefaultCacheKeyGenerator;
import org.jsr107.ri.annotations.DefaultCacheResolverFactory;
import org.jsr107.ri.annotations.InternalCacheInvocationContext;
import org.jsr107.ri.annotations.InternalCacheKeyInvocationContext;
import org.jsr107.ri.annotations.StaticCacheInvocationContext;
import org.jsr107.ri.annotations.StaticCacheKeyInvocationContext;
import org.nutz.aop.InterceptorChain;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(name="cacheContextSource", create="init")
@SuppressWarnings({"rawtypes", "unchecked"})
public class NutCacheLookupUtil extends AbstractCacheLookupUtil<InterceptorChain> {

    protected CacheKeyGenerator cacheKeyGenerator;
    protected CacheResolverFactory cacheResolverFactory;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    public void init() {
        cacheKeyGenerator = new DefaultCacheKeyGenerator();
        CacheManager cacheManager = Caching.getCachingProvider().getCacheManager();
        cacheResolverFactory = new DefaultCacheResolverFactory(cacheManager);
    }
    
    protected InternalCacheKeyInvocationContext<? extends Annotation> createCacheKeyInvocationContextImpl(StaticCacheKeyInvocationContext<? extends Annotation> staticCacheKeyInvocationContext,
                                                                                                          InterceptorChain invocation) {
        return new NutInternalCacheKeyInvocationContext(staticCacheKeyInvocationContext, invocation);
    }

    protected InternalCacheInvocationContext<? extends Annotation> createCacheInvocationContextImpl(StaticCacheInvocationContext<? extends Annotation> staticCacheInvocationContext,
                                                                                                    InterceptorChain invocation) {
        return new NutInternalCacheInvocationContext(staticCacheInvocationContext, invocation);
    }

    protected Class<?> getTargetClass(InterceptorChain invocation) {
        return invocation.getCallingObj().getClass().getSuperclass();
    }

    protected Method getMethod(InterceptorChain invocation) {
        return invocation.getCallingMethod();
    }

    protected <T> T getObjectByType(Class<T> type) {
        return ioc.get(type);
    }

    protected CacheKeyGenerator getDefaultCacheKeyGenerator() {
        return cacheKeyGenerator;
    }

    protected CacheResolverFactory getDefaultCacheResolverFactory() {
        return cacheResolverFactory;
    }

}
