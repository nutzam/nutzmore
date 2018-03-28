nutz-plugins-iocloader
==================================

简介(可用性:试用,维护者:wendal、[邓华锋](http://dhf.ink))
==================================

演示自定义IocLoader的用法

新增功能点：
* 定时任务、线程环境下使用Ioc
* 通过配置comboIocLoader.properties文件来配置相关加载配置
* 从数据库指定表中加载ioc配置

comboIocLoader.properties 配置
```Shell
#也可配置项目的MainModule
ioc.main.module=
#ioc常规配置
ioc.by=*js, ioc/dao.js, *anno, ink.dhf, org.nutz.integration.quartz,org.nutz.plugins.ioc.loader
#需要在ioc初始化后，单独调用的类以","分割
ioc.loader.classes=org.nutz.integration.quartz.NutQuartzCronJobFactory,org.nutz.plugins.ioc.loader.TestIocBean1
#ioc加载后初始化动作 前后顺序  用户可以自定义实现org.nutz.plugins.ioc.loader.chain.IocSetup 接口  类似于mvc环境下的org.nutz.mvc.Setup接口  只不过参数改成ioc
ioc.setup.first=
ioc.setup.last=
#ioc初始化后，后加入的IocLoader  以“,”分割
ioc.combo.loader=org.nutz.plugins.ioc.loader.dao.DaoIocLoader
```


定时任务、线程环境下使用Ioc方式
```Java
System.out.println(ThreadIocLoader.getIoc().get(PropertiesProxy.class, "config").get("db.url"));
```

DaoIocLoader 默认是从当前ioc的dao实例的数据源中 t_iocbean 表 取出数据  bean的名称对应的字段名(默认是nm),
  配置对应在的字段名(默认是val). 现在可通过配置文件daoIocLoader.properties来配置。

```Shell
ioc.dao.name=dao
ioc.table=t_iocbean
ioc.name.field=nm
ioc.value.field=val
```


 可在test里的db配置文件，指定数据库，创建默认的表字段，添加上测试数据，运行以上代码来进行测试,最终获取的是db配置文件里的db.url的值


 以上的测试数据nm和val字段是：


 config | {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : [ "custom/" ]
		}
	}