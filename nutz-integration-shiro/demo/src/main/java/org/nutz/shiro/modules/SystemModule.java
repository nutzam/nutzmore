package org.nutz.shiro.modules;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.shiro.SecurityUtils;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.ViewModel;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.shiro.DES;
import org.nutz.shiro.Result;
import org.nutz.shiro.ShiroDemo.SessionKeys;
import org.nutz.shiro.biz.acl.ShiroUserService;

/**
 * @author admin
 *
 * @email kerbores@gmail.com
 *
 */
@At("system")
@IocBean
public class SystemModule {

	@Inject
	ShiroUserService shiroUserService;

	protected void _addCookie(String name, String value, int age) {
		Cookie cookie = new Cookie(name, value);
		cookie.setPath("/");
		cookie.setMaxAge(age);
		Mvcs.getResp().addCookie(cookie);
	}

	protected String _getCookie(String name) {
		Cookie[] cookies = Mvcs.getActionContext().getRequest().getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (Strings.equals(cookie.getName(), name)) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	@At
	@POST
	@Ok("re:->:/user/list")
	public String login(@Param("user") String user, @Param("password") String password, @Param("rememberMe") boolean rememberMe, HttpSession session, ViewModel model,
			HttpServletRequest request) {
		Result result = shiroUserService.login(user, password);
		if (result.isSuccess()) {
			// 登录成功处理
			session.setAttribute(SessionKeys.USER_KEY, result.getData().get("loginUser"));
			if (rememberMe) {
				NutMap data = NutMap.NEW();
				data.put("user", user);
				data.put("password", password);
				data.put("rememberMe", rememberMe);
				_addCookie("kerbores", DES.encrypt(Json.toJson(data)), 24 * 60 * 60 * 365);
			}
			return null;
		}
		String cookie = _getCookie("kerbores");
		NutMap data = NutMap.NEW();
		if (!Strings.isBlank(cookie)) {
			data = Lang.map(DES.decrypt(cookie));
		}
		request.setAttribute("loginInfo", data);
		model.addv("error", result.getData().get("reason"));
		return "beetl:pages/login.html";
	}

	@At
	@Ok(">>:/")
	public Result logout(HttpSession session) {
		SecurityUtils.getSubject().logout();
		return Result.success();
	}
}
