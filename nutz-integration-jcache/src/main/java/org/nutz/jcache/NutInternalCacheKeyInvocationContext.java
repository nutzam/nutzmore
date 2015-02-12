package org.nutz.jcache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.jsr107.ri.annotations.AbstractInternalCacheKeyInvocationContext;
import org.jsr107.ri.annotations.StaticCacheKeyInvocationContext;
import org.nutz.aop.InterceptorChain;

public class NutInternalCacheKeyInvocationContext<A extends Annotation> extends AbstractInternalCacheKeyInvocationContext<InterceptorChain, A> {

    public NutInternalCacheKeyInvocationContext(StaticCacheKeyInvocationContext<A> staticCacheKeyInvocationContext, InterceptorChain invocation) {
        super(staticCacheKeyInvocationContext, invocation);
    }

    protected Object[] getParameters(InterceptorChain invocation) {
        return invocation.getArgs();
    }
    
    protected Method getMethod(InterceptorChain invocation) {
        return invocation.getCallingMethod();
    }
    
    protected Object getTarget(InterceptorChain invocation) {
        return invocation.getCallingObj();
    }
}
