package org.nutz.shiro.modules;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.nutz.integration.shiro.annotation.NutzRequiresPermissions;
import org.nutz.mvc.annotation.At;
import org.nutz.shiro.InstallPermission;
import org.nutz.shiro.Result;
import org.nutz.shiro.ext.anno.ThunderRequiresPermissions;

/**
 * @author admin
 *
 * @email kerbores@gmail.com
 *
 */
@At("shiro")
public class ShiroDemoModule extends AbstractBaseModule {

	@At
	@RequiresGuest
	public Result guest() {
		return Result.success().addData("tag", "guest");
	}

	@At
	@RequiresUser
	public Result user() {
		return Result.success().addData("tag", "user");
	}

	@At
	@RequiresAuthentication
	public Result authentication() {
		return Result.success().addData("tag", "authentication");
	}

	@At
	@RequiresRoles("admin")
	public Result admin() {
		return Result.success().addData("tag", "admin");
	}

	@At
	@RequiresRoles(value = { "admin", "kkk" }, logical = Logical.AND)
	public Result logical() {
		return Result.success().addData("tag", "logical");
	}

	@At
	@RequiresPermissions("user.list")
	public Result permission() {
		return Result.success().addData("tag", "permission");
	}

	@At
	@NutzRequiresPermissions(name = "Nutz", tag = "demo", value = { "user.list" })
	public Result nutz() {
		return Result.success().addData("tag", "nutz");
	}

	@At
	@ThunderRequiresPermissions(InstallPermission.CONFIG_ADD)
	public Result thunder() {
		return Result.success().addData("tag", "thunder");
	}

}
