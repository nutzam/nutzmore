package org.nutz.integration.shiro;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.JsonFormat;
import org.nutz.mvc.view.UTF8JsonView;

public class NutShiro {
	
    public static String DefaultLoginURL = "/user/login";
	
	public static boolean isAjax(ServletRequest req) {
		Enumeration<String> em = ((HttpServletRequest)req).getHeaderNames();
		while (em.hasMoreElements()) {
			String name = em.nextElement();
			if (name.equalsIgnoreCase("X-Requested-With"))
				return true;
		}
		return false;
	}
	
	public static void rendAjaxResp(ServletRequest req, ServletResponse resp, Object re) {
		try {
			((HttpServletResponse)resp).setCharacterEncoding("UTF-8");
			new UTF8JsonView(JsonFormat.compact()).render((HttpServletRequest)req, (HttpServletResponse)resp, re);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
