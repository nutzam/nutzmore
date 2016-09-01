### Sigar on Nutz 

简介(可用性:开发中)
==================================

深度集成sigar

### 客户端集成(Servlet)

本集成方式用于自行显示系统的状态

在web.xml中添加

```xml
	<servlet>
		<servlet-class>org.nutz.sigar.integration.servlet.SigarServlet</servlet-class>
		<servlet-name>sigar</servlet-name>
	</servlet>
	<servlet-mapping>
		<servlet-name>sigar</servlet-name>
		<url-pattern>/api/sigar</url-pattern>
	</servlet-mapping>
```

### CS集成(watchdog模式)

本集成方式用于将本机数据发送到远程服务器

- 在MainModule添加*sigar引用
```java
@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/",
										   "*anno", "net.wendal.nutzbook",
										   "*quartz",// 关联Quartz
										   "*async",
										   "*tx",
											"*sigar"})
```

-  在MainSetup的init方法添加启动代码
```java
	ioc.get(SigarClient.class); // 被监控的主机
	ioc.get(SigarServer.class); // 接受监控数据的主机
```
 
 **注意**: SigarClient会从conf(一般都定义为这个名字吧)读取2个属性:
```java
// sigar.properties
sigar.api=https://api.nutz.cn/v1/sigar/ping
sigar.token=AABBBCCDDEEFF
```