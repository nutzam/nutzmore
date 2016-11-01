package net.wendal.quartzdemo;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

import net.wendal.quartzdemo.bean.User;

public class MainSetup implements Setup {

    public void init(NutConfig nc) {
        Ioc ioc = nc.getIoc();
        Dao dao = ioc.get(Dao.class);
        Daos.createTablesInPackage(dao, getClass(), false);

        if (0 == dao.count(User.class)) {
            User user = new User();
            user.setName("admin");
            user.setSalt(R.UU32());
            user.setPassword(Lang.digest("SHA-256", user.getSalt() + "123456"));
            dao.insert(user);
        }
        
        // 触发quartz 工厂,将扫描job任务
        
        ioc.get(NutQuartzCronJobFactory.class);
    }

    public void destroy(NutConfig nc) {}

}
