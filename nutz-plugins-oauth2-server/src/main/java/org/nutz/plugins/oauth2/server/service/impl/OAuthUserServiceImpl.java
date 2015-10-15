package org.nutz.plugins.oauth2.server.service.impl;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.oauth2.server.entity.OAuthUser;
import org.nutz.plugins.oauth2.server.service.OAuthUserService;
import org.nutz.plugins.oauth2.server.service.PasswordHelper;
import org.nutz.service.EntityService;

@IocBean(name = "oAuthUserService", fields = { "dao" })
public class OAuthUserServiceImpl extends EntityService<OAuthUser> implements OAuthUserService {

	@Inject
	private PasswordHelper passwordHelper;

	/**
	 * 创建用户
	 * 
	 * @param user
	 */
	public OAuthUser createUser(OAuthUser user) {
		// 加密密码
		passwordHelper.encryptPassword(user);
		return dao().insert(user);
	}

	@Override
	public OAuthUser updateUser(OAuthUser user) {
		dao().update(user);
		return user;
	}

	@Override
	public void deleteUser(Long userId) {
		dao().delete(userId);
	}

	/**
	 * 修改密码
	 * 
	 * @param userId
	 * @param newPassword
	 */
	public void changePassword(Long userId, String newPassword) {
		OAuthUser user = dao().fetch(getEntityClass(), userId);
		user.setPassword(newPassword);
		passwordHelper.encryptPassword(user);
		updateUser(user);
	}

	@Override
	public OAuthUser findOne(Long userId) {
		return dao().fetch(getEntityClass(), userId);
	}

	@Override
	public List<OAuthUser> findAll() {
		return dao().query(getEntityClass(), null);
	}

	/**
	 * 根据用户名查找用户
	 * 
	 * @param username
	 * @return
	 */
	public OAuthUser findByUsername(String username) {
		return dao().fetch(getEntityClass(), Cnd.where("username", "=", username));
	}

	/**
	 * 验证登录
	 * 
	 * @param username
	 *            用户名
	 * @param password
	 *            密码
	 * @param salt
	 *            盐
	 * @param encryptpwd
	 *            加密后的密码
	 * @return
	 */
	public boolean checkUser(String username, String password, String salt, String encryptpwd) {
		String pwd = passwordHelper.encryptPassword(username, password, salt);
		return pwd.equals(encryptpwd);
	}
}
