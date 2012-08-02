package org.nutz.integration.shiro;

import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.aop.AnnotationsAuthorizingMethodInterceptor;

/**
 * 这个类的目的仅仅是暴露AnnotationsAuthorizingMethodInterceptor的assertAuthorized方法
 * @author wendal
 *
 */
class ShiroAnnotationsAuthorizingMethodInterceptor extends AnnotationsAuthorizingMethodInterceptor {
    
    public static final ShiroAnnotationsAuthorizingMethodInterceptor defaultAuth = new ShiroAnnotationsAuthorizingMethodInterceptor(); 

	public void assertAuthorized(MethodInvocation methodInvocation)
			throws AuthorizationException {
		super.assertAuthorized(methodInvocation);
	}
}