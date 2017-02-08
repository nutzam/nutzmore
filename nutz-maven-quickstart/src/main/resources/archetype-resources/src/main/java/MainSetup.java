package $package;

import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.net.ssl.SSLContext;

import org.nutz.dao.Dao;
import org.nutz.dao.util.Daos;
import org.nutz.el.opt.custom.CustomMake;
import org.nutz.integration.quartz.NutQuartzCronJobFactory;
import org.nutz.integration.shiro.NutShiro;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Encoding;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.plugins.slog.service.SlogService;
import org.quartz.Scheduler;


/**
 * Nutz内核初始化完成后的操作
 * 
 * @author wendal
 *
 */
public class MainSetup implements Setup {

	private static final Log log = Logs.get();
	
	public static PropertiesProxy conf;

    public void init(NutConfig nc) {

		// 获取Ioc容器及Dao对象
		Ioc ioc = nc.getIoc();

		// 初始化JedisAgent
		//JedisAgent jedisAgent = ioc.get(JedisAgent.class);

        Dao dao = ioc.get(Dao.class);
        
        // 为全部标注了@Table的bean建表
        Daos.createTablesInPackage(dao, getClass().getPackage().getName(), false);

		// 获取配置对象
		conf = ioc.get(PropertiesProxy.class, "conf");

		// 初始化SysLog,触发全局系统日志初始化
		ioc.get(SlogService.class).log("method", "system", null, "系统启动", false);

		// 初始化默认根用户
        

		// 获取NutQuartzCronJobFactory从而触发计划任务的初始化与启动
		ioc.get(NutQuartzCronJobFactory.class);

		// 权限系统初始化
		
		//ioc.get(HotPlug.class).setupInit();
	}

	public void destroy(NutConfig conf) {
	    //conf.getIoc().get(HotPlug.class).setupDestroy();
		// 非mysql数据库,或多webapp共享mysql驱动的话,以下语句删掉
		try {
			Mirror.me(Class.forName("com.mysql.jdbc.AbandonedConnectionCleanupThread")).invoke(null, "shutdown");
		} catch (Throwable e) {
		}
		// 解决quartz有时候无法停止的问题
		try {
			conf.getIoc().get(Scheduler.class).shutdown(true);
		} catch (Exception e) {
		}
		// 解决com.alibaba.druid.proxy.DruidDriver和com.mysql.jdbc.Driver在reload时报warning的问题
		// 多webapp共享mysql驱动的话,以下语句删掉
		Enumeration<Driver> en = DriverManager.getDrivers();
		while (en.hasMoreElements()) {
            try {
                Driver driver = en.nextElement();
                String className = driver.getClass().getName();
                log.debug("deregisterDriver: " + className);
                DriverManager.deregisterDriver(driver);
            }
            catch (Exception e) {
            }
        }
        try {
            MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName("com.alibaba.druid:type=MockDriver");
            if (mbeanServer.isRegistered(objectName))
                mbeanServer.unregisterMBean(objectName);
            objectName = new ObjectName("com.alibaba.druid:type=DruidDriver");
            if (mbeanServer.isRegistered(objectName))
                mbeanServer.unregisterMBean(objectName);
        } catch (Exception ex) {
        }
        
        // org.brickred.socialauth.util.HttpUtil 把一个内部类注册到SSLContext,擦!
        try {
            SSLContext.getDefault().init(null, null, new SecureRandom());
        }
        catch (Exception e) {
        }
	}
}
