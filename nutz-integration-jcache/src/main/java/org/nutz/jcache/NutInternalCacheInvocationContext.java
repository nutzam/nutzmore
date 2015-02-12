package org.nutz.jcache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.jsr107.ri.annotations.AbstractInternalCacheInvocationContext;
import org.jsr107.ri.annotations.StaticCacheInvocationContext;
import org.nutz.aop.InterceptorChain;

public class NutInternalCacheInvocationContext<A extends Annotation> extends AbstractInternalCacheInvocationContext<InterceptorChain, A> {

    public NutInternalCacheInvocationContext(StaticCacheInvocationContext<A> staticCacheInvocationContext, InterceptorChain invocation) {
        super(staticCacheInvocationContext, invocation);
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
