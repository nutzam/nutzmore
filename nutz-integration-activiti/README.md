Nutz集成Activiti 5.17+的插件
==================================

简介(可用性:试用,维护者:wendal)
==================================

提供Activiti相关的Ioc配置

用法
==================================

依赖的ioc bean : dataSource(数据库连接池)和conf(PropertiesProxy实例)

在conf扫描的配置目录下(通常叫custom,取决于conf的定义)添加activiti.properties

```
# activiti 相关配置,必须以activiti.开头. 详细配置项,可参看StandaloneProcessEngineConfiguration类的属性
activiti.databaseSchemaUpdate=true
activiti.asyncExecutorEnabled=true
activiti.asyncExecutorActivate=false
```

本插件的遵循通用插件ioc加载规则,所以仅需要在IocBy中添加```*activiti```即可.

```java
@IocBy(args="*js", "ioc/", 
		    "*anno", "net.wendal.nutzbook",
		    "*async",
		    "*tx",
		    "*activiti") // 添加activiti支持
```

在MainSetup的init方法内主动初始化

```java
ioc.get(ProcessEngine.class); 
```