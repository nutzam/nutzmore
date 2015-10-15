package org.nutz.plugins.oauth2.server.server.web;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.plugins.oauth2.server.entity.OAuthUser;
import org.nutz.plugins.oauth2.server.service.OAuthUserService;

@IocBean
@At("/user")
public class UserController {

	@Inject
	private OAuthUserService oAuthUserService;

	@At
	@Ok("jsp:jsp.user.list")
	public String list(HttpServletRequest req) {
		req.setAttribute("userList", oAuthUserService.findAll());
		return "user/list";
	}

	@At("/create")
	@Ok("jsp:jsp.user.edit")
	public String showCreateForm(HttpServletRequest req) {
		req.setAttribute("user", new OAuthUser());
		req.setAttribute("op", "新增");
		return "user/edit";
	}

	@At("/create")
	@Ok(">>:user")
	public String create(OAuthUser user, HttpServletRequest req) {
		oAuthUserService.createUser(user);
		req.setAttribute("msg", "新增成功");
		return "redirect:/user";
	}

	@At("/?/update")
	@Ok("jsp:jsp.user.edit")
	public String showUpdateForm(Long id, HttpServletRequest req) {
		req.setAttribute("user", oAuthUserService.findOne(id));
		req.setAttribute("op", "修改");
		return "user/edit";
	}

	@At("/?/update")
	@Ok(">>:user")
	public String update(OAuthUser user, HttpServletRequest req) {
		oAuthUserService.updateUser(user);
		req.setAttribute("msg", "修改成功");
		return "redirect:/user";
	}

	@At("/?/delete")
	@Ok("jsp:jsp.user.edit")
	public String showDeleteForm(Long id, HttpServletRequest req) {
		req.setAttribute("user", oAuthUserService.findOne(id));
		req.setAttribute("op", "删除");
		return "user/edit";
	}

	@At("/?/delete")
	@Ok(">>:user")
	public String delete(Long id, HttpServletRequest req) {
		oAuthUserService.deleteUser(id);
		req.setAttribute("msg", "删除成功");
		return "redirect:/user";
	}

	@At("/?/changePassword")
	@Ok("jsp:jsp.user.changePassword")
	public String showChangePasswordForm(Long id, HttpServletRequest req) {
		req.setAttribute("user", oAuthUserService.findOne(id));
		req.setAttribute("op", "修改密码");
		return "user/changePassword";
	}

	@At("/?/changePassword")
	@Ok(">>:user")
	public String changePassword(Long id, String newPassword, HttpServletRequest req) {
		oAuthUserService.changePassword(id, newPassword);
		req.setAttribute("msg", "修改密码成功");
		return "redirect:/user";
	}

}
