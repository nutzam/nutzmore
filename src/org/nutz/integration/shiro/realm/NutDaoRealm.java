package org.nutz.integration.shiro.realm;

import java.util.Enumeration;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.integration.shiro.realm.bean.User;
import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

/**
 * 用NutDao来实现Shiro的Realm
 * <p/> 可以通过配置文件注入数据源
 * <p/> 在Web环境中也可以通过自动搜索来获取NutDao
 * @author wendal
 *
 */
public class NutDaoRealm extends AuthorizingRealm {
	
	private static final Log log = Logs.get();
	
	protected Dao dao;

	protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
		String username = principalCollection.getPrimaryPrincipal().toString();
		User user = dao().fetch(User.class, Cnd.where("name", "=", username));
		if (user != null) {
			if (user.isLocked()) 
				throw new LockedAccountException("Account [" + username + "] is locked.");
		}
		dao().fetchLinks(user, null);
		SimpleAccount account = new SimpleAccount(username, "*", name);
		account.setRoles(user.getRoleStrSet());
		account.setStringPermissions(user.getPermissionStrSet());
		return account;
	}

	protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
		UsernamePasswordToken upToken = (UsernamePasswordToken) token;
		String passwd = new String(upToken.getPassword());
		String username = upToken.getUsername();
		User user = dao().fetch(User.class, Cnd.where("name", "=", username).and("passwd", "=", passwd));
		if (user != null) {
			if (user.isLocked()) 
				throw new LockedAccountException("Account [" + username + "] is locked.");
		}
		dao().fetchLinks(user, null);
		SimpleAccount account = new SimpleAccount(username, passwd, name);
		account.setRoles(user.getRoleStrSet());
		account.setStringPermissions(user.getPermissionStrSet());
		return account;
	}
	
	public NutDaoRealm() {
		this("nutz");
	}

	public NutDaoRealm(String name) {
		this.name = name;
	}
	
	private String name;
	
	@SuppressWarnings("unchecked")
	public Dao dao() {
		if (dao == null) {
			ServletContext servletContext = Mvcs.getServletContext();
			if (servletContext != null) {
				//也行我能直接拿到Ioc容器
				Ioc ioc = Mvcs.getIoc();
				if (ioc != null)
					dao = ioc.get(Dao.class, daoBeanName);
				else {
					//Search in servletContext.attr
					Enumeration<String> names = servletContext.getAttributeNames();
					while (names.hasMoreElements()) {
						String attrName = (String) names.nextElement();
						Object obj = servletContext.getAttribute(attrName);
						if (obj instanceof Ioc) {
							dao = ((Ioc)obj).get(Dao.class, daoBeanName);
							return dao;
						}
					}
					
					//还是没找到? 试试新版Mvcs.ctx
					ioc = Mvcs.ctx.getDefaultIoc();
					if (ioc != null) {
						dao = ioc.get(Dao.class, daoBeanName);
						return dao;
					}
				}
			}
			log.warn("No dao found!!");
			throw new RuntimeException("NutDao not found!!");
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
