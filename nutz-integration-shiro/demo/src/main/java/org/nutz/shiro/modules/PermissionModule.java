package org.nutz.shiro.modules;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.shiro.InstallPermission;
import org.nutz.shiro.Result;
import org.nutz.shiro.bean.Permission;
import org.nutz.shiro.biz.Pager;
import org.nutz.shiro.biz.acl.PermissionService;
import org.nutz.shiro.ext.anno.ThunderRequiresPermissions;

/**
 * 
 * @author 王贵源
 *
 * @email kerbores@kerbores.com
 *
 * @description 权限控制器
 * 
 * @copyright 内部代码,禁止转发
 *
 *
 * @time 2016年1月26日 下午3:37:37
 */
@At("permission")
public class PermissionModule extends AbstractBaseModule {

	@Inject
	private PermissionService permissionService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dgj.nutz.module.base.AbstractBaseModule#_getNameSpace()
	 */
	@Override
	public String _getNameSpace() {
		return "acl";
	}

	/**
	 * 列表
	 * 
	 * @param page
	 * @return
	 *
	 * @author 王贵源
	 */
	@At
	@Ok("beetl:pages/permission/list.html")
	@ThunderRequiresPermissions(InstallPermission.PERMISSION_LIST)
	public Result list(@Param(value = "page", df = "1") int page) {
		page = _fixPage(page);
		Pager<Permission> pager = permissionService.searchByPage(page);
		pager.setUrl(_base() + "/permission/list");
		return Result.success().addData("pager", pager).setTitle("权限列表");
	}

	/**
	 * 搜索
	 * 
	 * @param page
	 * @param key
	 * @return
	 */
	@At
	@Ok("beetl:pages/permission/list.html")
	@ThunderRequiresPermissions(InstallPermission.PERMISSION_LIST)
	public Result search(@Param(value = "page", df = "1") int page, @Param("key") String key) {
		page = _fixPage(page);
		key = _fixSearchKey(key);
		Pager<Permission> pager = permissionService.searchByKeyAndPage(key, page, "name", "description");
		pager.setUrl(_base() + "/permission/search");
		pager.addParas("key", key);
		return Result.success().addData("pager", pager).addData("key", key).setTitle("权限检索");
	}

}
