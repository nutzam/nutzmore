package org.nutz.plugins.undertow.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;

import org.nutz.mvc.Mvcs;

/**
 * 简化 cookie 的读写
 * 
 * @author qinerg@gmail.com
 * @varsion 2017-5-27
 */
public class Cookies {

	/**
	 * 获取 cookie信息.
	 *
	 * @param name 要获取的cookie名称.
	 * @return 该cooke的值. 如果不存在该cookie,返回null.
	 * @see #var(String, String)
	 * @see #var(String, String, int)
	 */
	public static String var(String name) {
		Cookie c[] = Mvcs.getReq().getCookies();
		if (c != null) {
			for (Cookie aC : c) {
				if (aC.getName().equals(name)) {
					try {
						return aC.getValue() == null ? null : URLDecoder.decode(aC.getValue(), "utf-8");
					} catch (Exception ignored) {
						return null;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 创建 cookie. 若value为空表示删除.
	 * <br/>注：所创建的cookie将在浏览器关闭后失效，且所创建的cookie只在当前的Context应用程序下有效.
	 *
	 * @param name  cookie名称
	 * @param value cookie值
	 */
	public static void var(String name, String value) {
		var(name, value, -1);
	}

	/**
	 * 创建 cookie 并设置cookie的生效时间.  若value为空表示删除.  <br/>注：所创建的cookie只在当前的Context应用程序下有效.
	 *
	 * @param name   cookie名称
	 * @param value  cookie值
	 * @param maxAge 以秒为单位，设置cookie的过期时间．(例如： 86400 = 60 x 60 x 24 = 1天)
	 * @see #var(String, String)
	 */
	public static void var(String name, String value, int maxAge) {
		if (name == null)
			return;
		if (value == null) {//for remove
			Cookie c[] = Mvcs.getReq().getCookies();
			if (c == null)
				return;
			for (Cookie aC : c)
				if (name.equals(aC.getName())) {
					aC.setMaxAge(0);
					aC.setPath("/");
					Mvcs.getResp().addCookie(aC);
					break;
				}
		} else {//for create
			try {
				Cookie c = new Cookie(name, java.net.URLEncoder.encode(value, "utf-8"));
				c.setMaxAge(maxAge == 0 ? -1 : maxAge);
				c.setPath("/");
				Mvcs.getResp().addCookie(c);
			} catch (UnsupportedEncodingException ignore) {
			}
		}
	}

}
