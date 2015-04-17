package org.nutz.integration.shiro;

import org.apache.shiro.authz.AuthorizationException;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.DefaultViewMaker;
import org.nutz.mvc.view.ServerRedirectView;

/**
 * 在入口方法中应用Shiro注解来进行安全过滤,建议使用NutShiroProcessor
 * @author wendal
 *
 */
@Deprecated
public class ShiroActionFilter extends NutShiroMethodInterceptor implements ActionFilter {

	public View match(final ActionContext actionContext) {
		try {
			assertAuthorized(new NutShiroInterceptor(actionContext));
		} catch (AuthorizationException e) {
			return whenAuthFail(actionContext, e);
		}
		return null;
	}

	private View view;
	
	public ShiroActionFilter() {
		view = new ServerRedirectView(NutShiro.DefaultLoginURL);
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


