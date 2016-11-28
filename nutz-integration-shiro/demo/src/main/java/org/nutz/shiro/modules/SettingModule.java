package org.nutz.shiro.modules;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Ok;
import org.nutz.shiro.Result;
import org.nutz.shiro.ShiroDemo.SessionKeys;
import org.nutz.shiro.bean.User;
import org.nutz.shiro.biz.acl.UserService;

@At("setting")
public class SettingModule extends AbstractBaseModule {

	@Inject
	UserService userService;

	@At
	@Ok("beetl:pages/user/detail.html")
	public Result profile(@Attr(SessionKeys.USER_KEY) User user) {
		return Result.success().setTitle("个人信息").addData("user", userService.fetch(user.getId()));
	}
}
