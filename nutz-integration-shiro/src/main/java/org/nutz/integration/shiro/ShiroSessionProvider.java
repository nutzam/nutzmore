package org.nutz.integration.shiro;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.nutz.mvc.SessionProvider;

/**
 * 代理Nutz内部使用Session的调用为Shiro的Shiro的session
 * @author wendal
 *
 */
public class ShiroSessionProvider implements SessionProvider {

	public HttpServletRequest filter(HttpServletRequest req, HttpServletResponse resp, ServletContext servletContext) {
		if (req instanceof ShiroHttpServletRequest)
			return req;
		return new ShiroHttpServletRequest(req, servletContext, true);
	}

	public void notifyStop() {
	}

}