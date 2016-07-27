Nutz集成Quartz的插件
======================

简介(可用性:生产)
==================================

集成Quartz(计划任务/定时任务)的不二选择

* 一个预定义的IocLoader,包含Quartz启动所需要的2个bean的定义
* 一个任务加载类(可选)

使用方法
-------------------------

* 添加quartz的jar, 支持2.x版本,建议用最新版
* 在src或maven的resources目录下添加一个 quartz.properties
* 在MainModule的IocBy中引用QuartzIocLoader
* 在需要使用quartz的类中引用Scheduler即可

供参考的quartz.properties,也是quartz最简单的配置
-----------------------------------------------

	org.quartz.scheduler.instanceName = NutzbookScheduler 
	org.quartz.threadPool.threadCount = 3 
	org.quartz.jobStore.class = org.quartz.simpl.RAMJobStore
	org.quartz.scheduler.skipUpdateCheck=true

示例IocBy配置
----------------------------------------------

	@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/",
										   "*anno", "net.wendal.nutzbook",
										   "*org.nutz.integration.quartz.QuartzIocLoader"})
										   
在Module或Service中引用Scheduler
-----------------------------------------------

	@Inject Scheduler scheduler;
	
	
任务加载类NutQuartzCronJobFactory(可选的)
-----------------------------------------------

本插件额外提供了一个简单的任务加载类,可以从conf这个ioc bean中读取cron开头的属性来启动任务

启用方式,在SetupBy的init方法中

	nc.getIoc().get(NutQuartzCronJobFactory.class)

配置文件的内容cron.properties, 通过用户自行定义的名为conf的PropertiesProxy实例加载.

	cron.net.wendal.nutzbook.quartz.job.CleanNonActiveUserJob=0 0/2 * * * ?
	
扫描注解的方式(Scheduled注解)

	cron.pkgs=net.wendal.nutzbook.quartz.job
	

详细用法请参考nutzbook中的相关描述

示例conf bean定义
-----------------------

可以单独一个js或者放在dao.js


单独一个conf.js文件

```
var ioc = {
        conf : {
            type : "org.nutz.ioc.impl.PropertiesProxy",
            fields : {
                paths : ["custom/"]
            }
        }
};
```

放置到dao.js中, 因为一般来说都有dao.js,而且dao这个bean通常也需要conf配置信息

示例来至nutzbook

```
var ioc = {
		conf : {
			type : "org.nutz.ioc.impl.PropertiesProxy",
			fields : {
				paths : ["custom/"]
			}
		},
	    dataSource : {
	        type : "com.alibaba.druid.pool.DruidDataSource",
	        events : {
	        	create : "init",
	            depose : 'close'
	        },
	        fields : {
	            url : {java:"$conf.get('db.url')"},
	            username : {java:"$conf.get('db.username')"},
	            password : {java:"$conf.get('db.password')"},
	            testWhileIdle : true,
	            validationQuery : {java:"$conf.get('db.validationQuery')"},
	            maxActive : {java:"$conf.get('db.maxActive')"},
	            filters : "mergeStat",
	            connectionProperties : "druid.stat.slowSqlMillis=2000"
	        }
	    },
		dao : {
			type : "org.nutz.dao.impl.NutDao",
			args : [{refer:"dataSource"}]
		}
};
```

	
	