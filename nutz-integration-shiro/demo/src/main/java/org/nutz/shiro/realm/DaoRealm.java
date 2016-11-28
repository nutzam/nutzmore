package org.nutz.shiro.realm;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.nutz.lang.Lang;
import org.nutz.mvc.Mvcs;
import org.nutz.shiro.bean.User;
import org.nutz.shiro.bean.User.Status;
import org.nutz.shiro.biz.acl.ShiroUserService;

/**
 * @author admin
 *
 * @email kerbores@gmail.com
 *
 */
public class DaoRealm extends AuthorizingRealm {

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String userName = upToken.getUsername();
		User user = getUserService().findByName(userName);
		if (Lang.isEmpty(user))// 用户不存在
			return null;
		if (user.getStatus() == Status.DISABLED)// 用户被锁定
			throw new LockedAccountException("Account [" + upToken.getUsername() + "] is locked.");

		SimpleAuthenticationInfo account = new SimpleAuthenticationInfo(user.getName(), user.getPassword(), getName());
		return account;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		String userName = principalCollection.getPrimaryPrincipal().toString();
		User user = getUserService().findByName(userName);
		if (user == null)// 用户不存在
			return null;
		if (user.getStatus() == Status.DISABLED)// 用户被锁定
			throw new LockedAccountException("Account [" + userName + "] is locked.");
		SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
		List<String> roleNameList = getUserService().getRolesInfo(user.getId());
		auth.addRoles(roleNameList);// 添加角色
		List<String> permissionNames = getUserService().getAllPermissionsInfo(user.getId());
		auth.addStringPermissions(permissionNames);// 添加权限
		return auth;
	}

	private ShiroUserService getUserService() {
		return Mvcs.getIoc().get(ShiroUserService.class);
	}

}
