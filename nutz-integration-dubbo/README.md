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
										   "*dubbo"})
```
								   
在MainSetup的init方法中加载
-----------------------------------------------

```java
ioc.get(DubboAnnotationLoader.class, "dubbo_anno");
// 该操作会触发生产者和消费者的初始化
```

配置项
-----------------------------------------------

配置项通过名为conf的IocBean获取, 通常是PropertiesProxy类的实例

```ini
dubbo.application.name=nutzbook

dubbo.registry.name=wendal
dubbo.registry.address=10.20.130.230:9090
dubbo.registry.username=wendal
dubbo.registry.password=root

dubbo.protocol.name=dubbo
dubbo.protocol.port=123456
dubbo.protocol.threads=256

dubbo.anno.packages=net.wendal.nutzbook
```