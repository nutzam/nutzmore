package org.nutz.integration.shiro;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.json.JsonFormat;
import org.nutz.mvc.view.UTF8JsonView;

/**
 * Nutz与Shiro集成所需要的一些辅助方法
 * @author wendal<wendal1985@gmail.com>
 *
 */
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
	
    public static boolean match(Method method) {
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
