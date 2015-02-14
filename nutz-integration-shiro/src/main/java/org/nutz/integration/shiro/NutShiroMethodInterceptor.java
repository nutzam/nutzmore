package org.nutz.integration.shiro;

import org.apache.shiro.authz.aop.AnnotationsAuthorizingMethodInterceptor;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.IocBean;

/**
 * 将Shiro注解,映射为NutAop的拦截器
 * 
 * @author wendal
 *
 */
@IocBean
public class NutShiroMethodInterceptor extends AnnotationsAuthorizingMethodInterceptor implements MethodInterceptor {

    public void filter(InterceptorChain chain) throws Throwable {
        assertAuthorized(new NutShiroInterceptor(chain));
    }

}
