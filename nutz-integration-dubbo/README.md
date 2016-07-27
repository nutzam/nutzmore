Nutz集成Dubbo的插件
======================

简介(可用性:开发中)
==================================

兼容原生dubbo配置文件

* 一个预定义的IocLoader,包含Dubbo启动所需要的bean的定义
* 一个Dubbo注解扫描器

使用方法
-------------------------

* 添加dubbo依赖及本插件的依赖
* 在MainModule的IocBy中直接 *dubbo
* 在MainSetup的init方法中获取

示例IocBy配置
----------------------------------------------

```java
	@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/",
										   "*anno", "net.wendal.nutzbook",
										   "*dubbo", "dubbo.xml"}) // dubbo.xml是配置文件的路径
```
								   
在MainSetup的init方法中加载
-----------------------------------------------

```java
	ioc.get(DubboMaster.class);
	// 该操作会触发Service的初始化
```

TODO
-------------------------------------------------

* 支持dubbo:parameter标签
* 支持dubbo:method标签
* 支持dubbo:argument标签
* 支持一次性初始化所有Bean
