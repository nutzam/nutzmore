package org.nutz.shiro.modules;

import java.util.List;

import org.nutz.dao.entity.Record;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;
import org.nutz.shiro.InstallPermission;
import org.nutz.shiro.Result;
import org.nutz.shiro.bean.Role;
import org.nutz.shiro.biz.Pager;
import org.nutz.shiro.biz.acl.RoleService;
import org.nutz.shiro.ext.anno.ThunderRequiresPermissions;

/**
 * 
 * @author 王贵源
 *
 * @email kerbores@kerbores.com
 *
 * @description 角色控制器
 * 
 * @copyright 内部代码,禁止转发
 *
 *
 * @time 2016年1月26日 下午3:38:21
 */
@At("role")
public class RoleModule extends AbstractBaseModule {

	@Inject
	private RoleService roleService;

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
	 * 添加角色页面
	 * 
	 * @return
	 */
	@At
	@GET
	@Ok("beetl:pages/role/add_edit.html")
	@ThunderRequiresPermissions(InstallPermission.ROLE_ADD)
	public Result add() {
		return Result.success().setTitle("添加角色");
	}

	/**
	 * 添加角色
	 * 
	 * @param role
	 *            待添加角色
	 * @return
	 */
	@At
	@POST
	@ThunderRequiresPermissions(InstallPermission.ROLE_ADD)
	public Result add(@Param("..") Role role) {
		if (null != roleService.fetch(role.getName())) {
			return Result.fail("角色" + role.getName() + "已存在");
		}
		role.setInstalled(false);
		role = roleService.save(role);
		return role == null ? Result.fail("添加角色失败") : Result.success().addData("role", role);
	}

	/**
	 * 删除角色
	 * 
	 * @param id
	 *            角色id
	 * @return
	 */
	@At("/delete/*")
	@ThunderRequiresPermissions(InstallPermission.ROLE_DELETE)
	public Result delete(int id) {
		return roleService.delete(id) == 1 ? Result.success() : Result.fail("删除失败!");
	}

	/**
	 * 编辑页码页面
	 * 
	 * @param id
	 *            角色id
	 * @return
	 */
	@At("/edit/*")
	@Ok("beetl:pages/role/add_edit.html")
	@ThunderRequiresPermissions(InstallPermission.ROLE_EDIT)
	public Result edit(int id) {
		Role role = roleService.fetch(id);
		return Result.success().addData("role", role).setTitle("编辑角色");
	}

	/**
	 * 授权页面
	 * 
	 * @param id
	 * @return
	 *
	 * @author 王贵源
	 */
	@At("/grant/*")
	@GET
	@Ok("beetl:pages/role/grant.html")
	@ThunderRequiresPermissions(InstallPermission.ROLE_GRANT)
	public Result grant(int id) {
		List<Record> records = roleService.findPermissionsWithRolePowerdInfoByRoleId(id);
		return Result.success().addData("records", records).addData("roleId", id).setTitle("角色授权");
	}

	/**
	 * ajax 授权
	 *
	 * @param ids
	 * @param roleId
	 * @return
	 *
	 * @author 王贵源
	 */
	@At
	@POST
	@ThunderRequiresPermissions(InstallPermission.ROLE_GRANT)
	public Result grant(@Param("permissions") int[] ids, @Param("id") int roleId) {
		return roleService.setPermission(ids, roleId);
	}

	/**
	 * 角色列表
	 * 
	 * @param page
	 *            页码
	 * @return
	 */
	@At
	@Ok("beetl:pages/role/list.html")
	@ThunderRequiresPermissions(InstallPermission.ROLE_LIST)
	public Result list(@Param(value = "page", df = "1") int page) {
		page = _fixPage(page);
		Pager<Role> pager = roleService.searchByPage(page);
		pager.setUrl(_base() + "/role/list");
		return Result.success().addData("pager", pager).setTitle("角色列表");
	}

	/**
	 * 搜索角色
	 * 
	 * @param page
	 *            页码
	 * @param key
	 *            关键词
	 * @return
	 */
	@At
	@Ok("beetl:pages/role/list.html")
	@ThunderRequiresPermissions(InstallPermission.ROLE_LIST)
	public Result search(@Param(value = "page", df = "1") int page, @Param("key") String key) {
		page = _fixPage(page);
		key = _fixSearchKey(key);
		Pager<Role> pager = roleService.searchByKeyAndPage(key, page, "name", "description");
		pager.setUrl(_base() + "/role/search");
		pager.addParas("key", key);
		return Result.success().addData("pager", pager).setTitle("角色检索");
	}

	/**
	 * 更新角色
	 * 
	 * @param role
	 *            待更新角色
	 * @return
	 */
	@At
	@POST
	@ThunderRequiresPermissions(InstallPermission.ROLE_EDIT)
	public Result update(@Param("..") Role role) {
		return roleService.update(role, "description") == 1 ? Result.success() : Result.fail("更新失败!");
	}
}
