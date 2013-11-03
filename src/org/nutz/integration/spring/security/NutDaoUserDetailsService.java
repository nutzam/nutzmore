package org.nutz.integration.spring.security;

import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.entity.Entity;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * 实现Spring-security的UserDetailsService和UserDetailsManager接口
 * @author wendal(wendal1985@gmail.com)
 *
 * @param <T> UserDetails的实现类,应当带有NutDao的注解信息
 */
public abstract class NutDaoUserDetailsService<T extends UserDetails> implements UserDetailsService, UserDetailsManager {

	protected Class<T> entryClass;
	protected Dao dao;
	protected String pkUsername;
	protected String pwdName;
	
	@SuppressWarnings("unchecked")
	public NutDaoUserDetailsService() {
		try {
			// 通过获取自身的泛型,得到用户自定义的UserDetails实现
            entryClass = (Class<T>) Mirror.getTypeParam(getClass(), 0);
        }
        catch (Throwable e) {
            throw Lang.wrapThrow(e);
        }
	}
	
	public void setDao(Dao dao) {
		this.dao = dao;
		Entity<T> en = dao.getEntity(entryClass);
		// 其username总应该是主键,例如标记了@Name
		pkUsername = en.getNameField().getName();
		// 需要猜测password字段(字段名其实也是猜的)的java属性名
		// TODO 通过查表来推测? 派生类自行指定也许更好
		if (en.getField("pwd") != null)
			pwdName = "pwd";
		else if (en.getField("passwd") != null)
			pwdName = "passwd";
		else
			pwdName = "password";
	}
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		T t = dao.fetch(entryClass, username);
		if (t == null) // Spring-security要求找不到用户时要抛出UsernameNotFoundException
			throw new UsernameNotFoundException(username + " not exist");
		return dao.fetchLinks(t, null); // UserDetails 也许带关联关系,一并fetch出
	}

	public void changePassword(String username, String password) {
		// 这是唯一用到pwdName的地方, 我猜
		dao.update(entryClass, Chain.make(pwdName, password), Cnd.where(pkUsername, "=", username));
	}

	public void createUser(UserDetails usr) {
		dao.insertWith(usr, null); // 这里其实没想好, 其关联关系是否应该也一并插入呢?
	}

	public void deleteUser(String username) {
		dao.delete(entryClass, username);
	}

	public void updateUser(UserDetails user) {
		dao.update(user);
	}

	public boolean userExists(String username) {
		return 0 != dao.count(entryClass, Cnd.where(pkUsername, "=", username));
	}

}
