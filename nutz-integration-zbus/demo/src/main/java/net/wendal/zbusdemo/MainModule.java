package net.wendal.zbusdemo;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;

@IocBy(args = {"*js", "ioc/", 
               "*anno", 
               "net.wendal.zbusdemo", 
               "*tx", 
               "*async", 
               // 加载zbus插件
               "*zbus", "net.wendal.zbusdemo"})
@Modules(scanPackage = true)
@SetupBy(MainSetup.class)
public class MainModule {}
