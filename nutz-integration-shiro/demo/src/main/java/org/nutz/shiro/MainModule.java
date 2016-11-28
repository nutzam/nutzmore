package org.nutz.shiro;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.beetl.ext.nutz.BeetlViewMaker;
import org.nutz.integration.shiro.ShiroSessionProvider;
import org.nutz.integration.shiro.annotation.NutzRequiresPermissions;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.ChainBy;
import org.nutz.mvc.annotation.Encoding;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.SessionBy;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.shiro.ShiroDemo.SessionKeys;
import org.nutz.shiro.bean.User;

/**
 * @author admin
 *
 * @email kerbores@gmail.com
 *
 */
@IocBean
@Ok("json")
@Fail("http:500")
@SetupBy(MainSetup.class) // 启动
@Modules(scanPackage = true)
@Views({ BeetlViewMaker.class }) // beetl
@SessionBy(ShiroSessionProvider.class)
@Encoding(input = "UTF-8", output = "UTF-8")
@ChainBy(type = MainChainMaker.class, args = {}) // 自定义shiro注解处理器
@IocBy(type = ComboIocProvider.class, args = { "*anno", "org.nutz", "*tx", "*js", "ioc", "*async" })
public class MainModule {

	@At
	@NutzRequiresPermissions(value = "admin", name = "a", tag = "b")
	public Result index() {
		return Result.success();
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

	@At("/")
	@Ok("re:beetl:pages/login.html")
	public String home(HttpServletRequest request, @Attr(SessionKeys.USER_KEY) User user) {
		if (user != null) {
			return ">>:/user/list";
		}
		String cookie = _getCookie("kerbores");
		NutMap data = NutMap.NEW();
		if (!Strings.isBlank(cookie)) {
			data = Lang.map(DES.decrypt(cookie));
		}
		request.setAttribute("loginInfo", data);
		return null;
	}

	@At
	@Ok("beetl:pages/test.html")
	public NutMap test() {
		return NutMap.NEW();
	}

}
