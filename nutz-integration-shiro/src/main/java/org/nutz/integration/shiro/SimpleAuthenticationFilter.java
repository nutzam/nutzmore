package org.nutz.integration.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 直接穿透
 * @author wendal
 *
 */
public class SimpleAuthenticationFilter extends org.apache.shiro.web.filter.authc.AuthenticationFilter {

	protected boolean isLoginRequest(ServletRequest request, ServletResponse response) {
		return false;
	}

	protected boolean isLoginSubmission(ServletRequest request, ServletResponse response) {
		return false;
	}

	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
	    saveRequestAndRedirectToLogin(request, response);
		return false;
	}
	
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		if (pathsMatch(getLoginUrl(), request))
			return true;
		return super.isAccessAllowed(request, response, mappedValue);
	}
	
	public void setLoginUrl(String loginUrl) {
	    super.setLoginUrl(loginUrl);
	    NutShiro.DefaultLoginURL = loginUrl;
	}
}
