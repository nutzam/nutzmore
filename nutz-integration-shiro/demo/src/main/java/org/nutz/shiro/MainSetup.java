package org.nutz.shiro;

import java.nio.charset.Charset;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.Ioc;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.Encoding;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.shiro.bean.Permission;
import org.nutz.shiro.bean.Role;
import org.nutz.shiro.bean.RolePermission;
import org.nutz.shiro.bean.User;
import org.nutz.shiro.bean.User.Status;
import org.nutz.shiro.bean.UserRole;
import org.nutz.shiro.biz.acl.PermissionService;
import org.nutz.shiro.biz.acl.RolePermissionService;
import org.nutz.shiro.biz.acl.RoleService;
import org.nutz.shiro.biz.acl.UserRoleService;
import org.nutz.shiro.biz.acl.UserService;

/**
 * @author admin
 *
 * @email kerbores@gmail.com
 *
 */
public class MainSetup implements Setup {
	private static final Log log = Logs.get();
	Role admin;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.nutz.mvc.Setup#destroy(org.nutz.mvc.NutConfig)
	 */
	@Override
	public void destroy(NutConfig nc) {

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.nutz.mvc.Setup#init(org.nutz.mvc.NutConfig)
	 */
	@Override
	public void init(NutConfig nc) {
		NutShiro.DefaultLoginURL = "/";
		NutShiro.DefaultNoAuthURL = "/403.jsp";

		if (!Charset.defaultCharset().name().equalsIgnoreCase(Encoding.UTF8)) {
			log.warn("This project must run in UTF-8, pls add -Dfile.encoding=UTF-8 to JAVA_OPTS");
		}

		Ioc ioc = nc.getIoc();

		Dao dao = ioc.get(Dao.class);
		Daos.FORCE_WRAP_COLUMN_NAME = true;

		// 创建和修改数据表
		Daos.createTablesInPackage(dao, User.class, false);
		Daos.migration(dao, User.class, true, true);

		final UserService userService = ioc.get(UserService.class);
		final RoleService roleService = ioc.get(RoleService.class);
		final PermissionService permissionService = ioc.get(PermissionService.class);
		final UserRoleService userRoleService = ioc.get(UserRoleService.class);
		final RolePermissionService rolePermissionService = ioc.get(RolePermissionService.class);

		Lang.each(InstalledRole.values(), new Each<InstalledRole>() {// 内置角色

			@Override
			public void invoke(int index, InstalledRole role, int length) throws ExitLoop, ContinueLoop, LoopException {
				if (roleService.fetch(Cnd.where("name", "=", role.getName())) == null) {
					Role temp = new Role();
					temp.setName(role.getName());
					temp.setDescription(role.getDescription());
					roleService.save(temp);
				}
			}
		});

		admin = roleService.fetch(Cnd.where("name", "=", InstalledRole.SU.getName()));

		if (admin == null) {// 这里理论上是进不来的,防止万一吧
			admin = new Role();
			admin.setName(InstalledRole.SU.getName());
			admin.setDescription(InstalledRole.SU.getDescription());
			admin = roleService.save(admin);
		}

		Lang.each(InstallPermission.values(), new Each<InstallPermission>() {// 内置权限

			@Override
			public void invoke(int index, InstallPermission permission, int length) throws ExitLoop, ContinueLoop, LoopException {
				Permission temp = null;
				if ((temp = permissionService.fetch(Cnd.where("name", "=", permission.getName()))) == null) {
					temp = new Permission();
					temp.setName(permission.getName());
					temp.setDescription(permission.getDescription());
					temp = permissionService.save(temp);
				}

				// 给SU授权
				if (rolePermissionService.fetch(Cnd.where("permissionId", "=", temp.getId()).and("roleId", "=", admin.getId())) == null) {
					RolePermission rp = new RolePermission();
					rp.setRoleId(admin.getId());
					rp.setPermissionId(temp.getId());
					rolePermissionService.save(rp);
				}
			}
		});

		User surperMan = null;
		if ((surperMan = userService.fetch(Cnd.where("name", "=", "admin"))) == null) {
			surperMan = new User();
			surperMan.setEmail("kerbores@zhcs.club");
			surperMan.setName("admin");
			surperMan.setPassword(Lang.md5("123456"));
			surperMan.setPhone("18996359755");
			surperMan.setRealName("王贵源");
			surperMan.setStatus(Status.ACTIVED);
			surperMan = userService.save(surperMan);
		}

		UserRole ur = null;
		if ((ur = userRoleService.fetch(Cnd.where("userId", "=", surperMan.getId()).and("roleId", "=", admin.getId()))) == null) {
			ur = new UserRole();
			ur.setUserId(surperMan.getId());
			ur.setRoleId(admin.getId());
			userRoleService.save(ur);
		}
	}

}
