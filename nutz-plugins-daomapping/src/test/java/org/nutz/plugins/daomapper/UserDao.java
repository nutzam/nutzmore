package org.nutz.plugins.daomapper;

import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.plugins.daomapper.bean.Role;
import org.nutz.plugins.daomapper.bean.User;


public interface UserDao extends Dao {

	User fetchById(int id);
	
	List<User> queryByName(String name);
	
	User fetchUserById(int id);
	
	Role fetchRoleById(int id);
}
