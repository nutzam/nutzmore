package org.nutz.integration.shiro;

import java.lang.reflect.Method;

import org.apache.shiro.aop.MethodInvocation;
import org.apache.shiro.authz.AuthorizationException;
import org.nutz.lang.Lang;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.DefaultViewMaker;
import org.nutz.mvc.view.ServerRedirectView;

/**
 * 在入口方法中应用Shiro注解来进行安全过滤
 * @author wendal
 *
 */
public class ShiroActionFilter implements ActionFilter {

	public View match(final ActionContext actionContext) {
		try {
			ShiroAnnotationsAuthorizingMethodInterceptor.defaultAuth.assertAuthorized(new MethodInvocation() {
				
				public Object proceed() throws Throwable {
					throw Lang.noImplement();
				}
				public Object getThis() {
					return actionContext.getModule();
				}
				public Method getMethod() {
					return actionContext.getMethod();
				}
				
				public Object[] getArguments() {
					return actionContext.getMethodArgs();
				}
			});
		} catch (AuthorizationException e) {
			return whenAuthFail(actionContext, e);
		}
		return null;
	}

	private View view;
	
	public ShiroActionFilter() {
		view = new ServerRedirectView("/login.html");
	}
	
	public ShiroActionFilter(String view) {
		if (view.contains(":")) {
			String[] vs = view.split(":", 2);
			this.view = new DefaultViewMaker().make(null, vs[0], vs[1]);
		} else {
			this.view = new ServerRedirectView(view);
		}
	}
	
	protected View whenAuthFail(ActionContext ctx, AuthorizationException e) {
		return view;
	}
}


