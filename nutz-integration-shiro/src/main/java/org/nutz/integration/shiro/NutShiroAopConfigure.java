package org.nutz.integration.shiro;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
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
        if (method.getAnnotation(RequiresRoles.class) != null 
                || method.getAnnotation(RequiresAuthentication.class) != null
                || method.getAnnotation(RequiresGuest.class) != null
                || method.getAnnotation(RequiresPermissions.class) != null
                || method.getAnnotation(RequiresUser.class) != null) {
            return true;
        }
        return false;
    }

}
