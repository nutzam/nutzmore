package org.nutz.integration.shiro.realm;

import javax.sql.DataSource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.integration.shiro.realm.bean.User;
import org.nutz.mvc.Mvcs;

/**
 * 用NutDao来实现Shiro的Realm
 * <p/> 可以通过配置文件注入数据源
 * <p/> 在Web环境中也可以通过自动搜索来获取NutDao
 * @author wendal
 *
 */
public class NutDaoRealm extends AuthorizingRealm {
	
	protected Dao dao;

	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		String username = principalCollection.getPrimaryPrincipal().toString();
		User user = dao().fetch(User.class, Cnd.where("name", "=", username));
        if (user == null)
            return null;
        if (user.isLocked()) 
            throw new LockedAccountException("Account [" + username + "] is locked.");
        
        SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
        auth.setRoles(user.getRoleStrSet());
        auth.addStringPermissions(user.getPermissionStrSet());
        
        return auth;
	}

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		User user = dao().fetch(User.class, Cnd.where("name", "=", upToken.getUsername()));
        if (user == null)
            return null;
        if (user.isLocked()) 
            throw new LockedAccountException("Account [" + upToken.getUsername() + "] is locked.");
        dao().fetchLinks(user, null);
        SimpleAccount account = new SimpleAccount(upToken.getUsername(), user.getPasswd(), name);
        if (user.getSalt() != null)
            account.setCredentialsSalt(ByteSource.Util.bytes(user.getSalt()));
        return account;
	}
	
	public NutDaoRealm() {
		this("nutz");
	}

	public NutDaoRealm(String name) {
		this.name = name;
	}
	
	private String name;
	
	public Dao dao() {
		if (dao == null) {
			dao = Mvcs.ctx().getDefaultIoc().get(Dao.class, daoBeanName);
			return dao;
		}
		return dao;
	}
	
	public void setDao(Dao dao) {
		this.dao = dao;
	}
	
	public void setDataSource(DataSource dataSource) {
		this.dao = new NutDao(dataSource);
	}
	
	private String daoBeanName = "dao";
	
	public void setDaoBeanName(String daoBeanName) {
		this.daoBeanName = daoBeanName;
	}
	
}
