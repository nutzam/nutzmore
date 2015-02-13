package org.nutz.integration.shiro;

import java.lang.reflect.Method;

import org.apache.shiro.aop.MethodInvocation;
import org.nutz.aop.InterceptorChain;
import org.nutz.mvc.ActionContext;

public class NutShiroInterceptor implements MethodInvocation {
    InterceptorChain chain;
    ActionContext ac;

    public NutShiroInterceptor(InterceptorChain chain) {
        this.chain = chain;
    }

    public NutShiroInterceptor(ActionContext ac) {
        this.ac = ac;
    }

    public Object proceed() throws Throwable {
        if (chain != null)
            return chain.doChain().getReturn();
        return null;
    }

    public Object getThis() {
        if (chain != null)
            return chain.getCallingObj();
        return ac.getModule();
    }

    public Method getMethod() {
        if (chain != null)
            return chain.getCallingMethod();
        return ac.getMethod();
    }

    public Object[] getArguments() {
        if (chain != null)
            return chain.getArgs();
        return ac.getMethodArgs();
    }
}
