package org.nutz.integration.shiro;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.nutz.aop.MethodMatcher;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.config.AopConfigration;
import org.nutz.ioc.aop.config.InterceptorPair;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(name="$aop_shiro")
public class NutShiroAopConfigure implements AopConfigration, MethodMatcher {

    public List<InterceptorPair> getInterceptorPairList(Ioc ioc, Class<?> klass) {
        List<InterceptorPair> list = new ArrayList<InterceptorPair>();
        list.add(new InterceptorPair(new NutShiroMethodInterceptor(), this));
        return list;
    }

    public boolean match(Method method) {
        for (Annotation anno : method.getAnnotations()) {
            if (anno.getClass().getName().startsWith("org.apache.shiro.authz.annotation."))
                return true;
        }
        return false;
    }

}
