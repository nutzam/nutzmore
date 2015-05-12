package org.nutz.plugins.cache.dao;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.cache.dao.meta.User;
import org.nutz.plugins.cache.dao.meta.UserProfile;

public class CachedNutDaoExecutorTest {
    
    private static final Log log = Logs.get();
    
    Ioc ioc;
    Dao dao;

    @Before
    public void setUp() throws Exception {
        CachedNutDaoExecutor.DEBUG = true;
        ioc = new NutIoc(new JsonLoader("ioc/dao.js"));
        dao = ioc.get(Dao.class);
        Daos.createTablesInPackage(dao, getClass().getPackage().getName(), true);
        
        User admin = new User();
        admin.setName("wendal");
        admin.setEmail("vt400@qq.com");
        UserProfile profile = new UserProfile();
        profile.setLocation("canada");
        profile.setSex("man");
        admin.setProfile(profile);
        dao.insert(admin);
        profile.setUserId(admin.getId());
        dao.insert(profile);
        
        assertTrue(dao.count(UserProfile.class) == 1);
    }

    @After
    public void tearDown() throws Exception {
        if (ioc != null)
            ioc.depose();
    }

    @Test
    public void test_simple() {
        dao.query(User.class, null);
        log.debug("query from cache=============");
        List<User> users = dao.query(User.class, null);
        log.debug("fetch UserProfile from DataBase");
        dao.fetchLinks(users, null); // 这里会查数据库
        
        // 现在全部数据都在缓存里面了,所以再查也是在缓存
        log.debug("all we need is in cache");
        // 再查一次
        users = dao.query(User.class, null);
        dao.fetchLinks(users, null); // 这里也不查数据库了
    }

}
