package net.wendal.activitidemo;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;

@IocBy(args = {"*js", "ioc/", 
               "*anno", 
               "net.wendal.activitidemo", 
               "*tx", 
               "*async", 
               // 加载activiti插件
               "*activiti"})
@Modules(scanPackage = true)
@SetupBy(MainSetup.class)
public class MainModule {}
