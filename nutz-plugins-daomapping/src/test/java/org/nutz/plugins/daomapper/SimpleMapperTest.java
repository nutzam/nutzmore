package org.nutz.plugins.daomapper;

import java.util.List;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.util.Daos;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;
import org.nutz.plugins.daomapper.bean.Role;
import org.nutz.plugins.daomapper.bean.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SimpleMapperTest {

    protected Dao dao;
    protected DruidDataSource ds;

    @Before
    public void before() {
        ds = new DruidDataSource();
        ds.setUrl("jdbc:h2:mem:~/daomapping");
        dao = new NutDao(ds);
        Daos.createTablesInPackage(dao, User.class, false);
        if (dao.count(User.class, Cnd.where("name", "=", "admin")) == 0) {
            User admin = new User();
            admin.setName("admin");
            admin.setSalt(R.UU32());
            admin.setPassword(Lang.md5("123456" + admin.getSalt()));
            dao.insert(admin);
        }
        if (dao.count(Role.class) == 0) {
            Role admin = new Role();
            admin.setName("admin");
            admin.setAlias("系统管理员");
            admin.setDescription("系统最高角色");
            dao.insert(admin);
        }
    }

    @After
    public void after() {
        if (ds != null)
            ds.close();
    }

	@Test
	public void testMap() {
		// UserDao 只是个接口
		UserDao us = SimpleMapper.map(dao, User.class, UserDao.class);
		User user = us.fetchById(1);
		assertNotNull(user);

		List<User> list = us.queryByName("admin");
		assertNotNull(list);
		assertTrue(list.size() > 0);

		assertEquals(user.getName(), us.fetchUserById(1).getName());

		assertNotNull(us.fetchRoleById(1));

		assertTrue(us.count(User.class) > 0);
	}

}
