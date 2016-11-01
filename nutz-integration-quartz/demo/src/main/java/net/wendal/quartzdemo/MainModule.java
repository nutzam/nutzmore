package net.wendal.quartzdemo;

import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.SetupBy;

@IocBy(args = {"*js", "ioc/", 
               "*anno", "net.wendal.quartzdemo", 
               "*tx", 
               "*async",
               "*quartz"}) // 添加quartz插件引用
@Modules(scanPackage = true)
@SetupBy(MainSetup.class)
public class MainModule {}
