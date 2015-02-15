package org.nutz.integration.shiro.realm;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.integration.shiro.realm.bean.Permission;
import org.nutz.integration.shiro.realm.bean.Role;
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

	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }
		String username = principals.getPrimaryPrincipal().toString();
		User user = dao().fetch(User.class, Cnd.where("name", "=", username));
        if (user == null)
            return null;
        if (user.isLocked()) 
            throw new LockedAccountException("Account [" + username + "] is locked.");

        SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
        user = dao.fetchLinks(user, null);
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                auth.addRole(role.getName());
                role = dao.fetchLinks(role, null);
                if (role.getPermissions() != null) {
                    for (Permission p : role.getPermissions()) {
                        auth.addStringPermission(p.getName());
                    }
                }
            }
        }
        if (user.getPermissions() != null) {
            for (Permission p : user.getPermissions()) {
                auth.addStringPermission(p.getName());
            }
        }
        
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
        SimpleAccount account = new SimpleAccount(upToken.getUsername(), user.getPasswd(), getName());
        if (user.getSalt() != null)
            account.setCredentialsSalt(ByteSource.Util.bytes(user.getSalt()));
        return account;
	}
	
	public NutDaoRealm() {
        super();
    }

    public NutDaoRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
        super(cacheManager, matcher);
    }

    public NutDaoRealm(CacheManager cacheManager) {
        super(cacheManager);
    }

    public NutDaoRealm(CredentialsMatcher matcher) {
        super(matcher);
    }

    public Dao dao() {
		if (dao == null) {
			dao = Mvcs.ctx().getDefaultIoc().get(Dao.class, "dao");
			return dao;
		}
		return dao;
	}
}
