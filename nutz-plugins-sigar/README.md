### Sigar on Nutz 

简介(可用性:试用,维护者:王贵源)
==================================

深度集成sigar
## 集成

### 添加依赖
 
``` xml
		<dependency>
            <groupId>org.nutz</groupId>
            <artifactId>nutz-plugins-sigar</artifactId>
            <version>1.r.58-SNAPSHOT</version>
        </dependency>
```

### 添加二进制文件
 
 + 全量
     拷贝resources目录下的全部二进制文件到classPath下
 + 指定平台
		拷贝resources目录下指定平台的二进制文件到classPath下
 + 二进制文件放置
       1 web项目,可直接放置到WEB-INF下lib目录中即可
       2 非web项目直接放入任意classPath即可 				
       ```java 
      	 System.getProperty("java.class.path") ;//可获取classpath
       ```

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

## 使用

### Servlet集成
 访问servlet配置的url即可获取当前应用运行环境的系统信息,可直接参照nutz-onekey项目进行单节点实时运行状态监测,或者通过集中化的监控中心来对接此信息.
### watchDog模式
此方式集成将定时向指定地址推送运行状态信息,可直接根据信息进行运维相关工作的开展.
提供一种实现思路,将上报信息对接mq的生产者,将监控规则对接mq的消费者,直接可实现监控功能.
