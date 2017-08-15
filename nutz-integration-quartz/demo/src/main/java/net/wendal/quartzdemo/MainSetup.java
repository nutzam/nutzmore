package net.wendal.quartzdemo;

import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;

public class MainSetup implements Setup {

    public void init(NutConfig nc) {
        Ioc ioc = nc.getIoc();

        // 触发quartz 工厂,将扫描job任务
        ioc.get(NutQuartzCronJobFactory.class);
    }

    public void destroy(NutConfig nc) {}

}
