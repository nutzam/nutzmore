package org.nutz.plugins.oauth2.server.server.web;

import javax.servlet.http.HttpServletRequest;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.plugins.oauth2.server.entity.OAuthClient;
import org.nutz.plugins.oauth2.server.service.OAuthClientService;

@IocBean
@At("/client")
public class ClientController {

	@Inject
	private OAuthClientService oAuthClientService;

	@At("/")
	@Ok("jsp:jsp.client.list")
	public void list(HttpServletRequest req) {
		req.setAttribute("clientList", oAuthClientService.findAll());
	}

	@At("/edit")
	@Ok("jsp:jsp.client.edit")
	public void showCreateForm(HttpServletRequest req) {
		req.setAttribute("client", new org.nutz.plugins.oauth2.server.entity.OAuthClient());
		req.setAttribute("op", "新增");
	}

	@At("/create")
	@Ok("jsp:jsp.client.edit")
	@POST
	public void create(@Param("::client.") OAuthClient client, HttpServletRequest req) {
		oAuthClientService.createClient(client);
		req.setAttribute("msg", "新增成功");
	}

	@At("/?/update")
	@Ok("jsp:jsp.client.edit")
	public void showUpdateForm(Long id, HttpServletRequest req) {
		req.setAttribute("client", oAuthClientService.findOne(id));
		req.setAttribute("op", "修改");
	}

	@At("/?/update")
	@Ok(">>:client")
	@POST
	public void update(OAuthClient client, HttpServletRequest req) {
		oAuthClientService.updateClient(client);
		req.setAttribute("msg", "修改成功");
	}

	@At(value = "/?/delete")
	@Ok(">>:client/edit")
	public void showDeleteForm(Long id, HttpServletRequest req) {
		req.setAttribute("client", oAuthClientService.findOne(id));
		req.setAttribute("op", "删除");
	}

	@At(value = "/?/delete")
	@POST
	@Ok(">>:client")
	public void delete(Long id, HttpServletRequest req) {
		oAuthClientService.deleteClient(id);
		req.setAttribute("msg", "删除成功");
	}

}
