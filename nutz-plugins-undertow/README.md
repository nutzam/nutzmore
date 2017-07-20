
nutz-plugins-undertow 集成了JBOSS Undertow内嵌式高性能web服务器插件
==================================


简介(可用性:试用,维护者:qinerg)
==================================

集成JBOSS Undertow高性能web服务器插件

可一键将您的web程序启动，无需繁琐的部署过程。

nutz-plugins-undertow 是一个集成了JBOSS Undertow内嵌式高性能web服务器插件。Undertow 是红帽公司的开源产品，是 [Wildfly](http://www.oschina.net/p/wildfly) 默认的 web 服务器。

Undertow 基于java nio开发，非常轻量，将本插件、nutz及所有依赖包全算上，也仅有4.93M，尚不及apache-tomcat-embed自身的大小。

同时，各方面压测结果显示，Undertow性能秒杀tomcat、jetty，甚至直追netty 。



## 用法

工程中添加插件依赖：

```
<dependency>
	<groupId>org.nutz</groupId>
	<artifactId>nutz-plugins-undertow</artifactId>
	<version>1.r.62-SNAPSHOT</version>
</dependency>
```

启动：

+ 方式一：直接使用org.nutz.plugins.undertow.WebLauncher,该类自带main方法
+ 方式二：新建一个类,调用org.nutz.plugins.undertow.WebLauncher

~~~java
package com.xxx.demo;

import org.nutz.plugins.undertow.WebLauncher;

public class MainLauncher extends WebLauncher {
    public static void main(String[] args) {
        WebLauncher.main(args);
    }
}
~~~

如果不指定配置的话，默认加载插件包自带的主模块org.nutz.plugins.undertow.welcome.DefaultModule。

运行后访问 http://127.0.0.1:8080/ 可看到欢迎页面。



## 插件的配置

用户可通过通过两种方式对插件配置：

+ 命令行参数
+ 配置文件
+ 通过代码直接指定参数

几种方式配置的属性是一致的。下面以配置文件为例说明。在源码根目示新建一个配置文件web.properties：

```
# 绑定请求的IP，默认 0.0.0.0
web.ip=0.0.0.0
# 应用监听的端口，默认 8080
web.port=8080
# 应用ContextPath，默认 /
web.path=/
# 应用的资源文件路径，默认 src/main/webapp/
web.root=src/main/webapp/
# 应用nutz mvc主模块，默认 org.nutz.plugins.undertow.welcome.DefaultModule , 用于演示. 请替换
web.modules=org.nutz.plugins.undertow.welcome.DefaultModule
# session缓存时长，默认 20
web.session=20
# 应用的运行模式，默认 dev ，暂时没有实际使用
web.runmode=dev
# web.thread.io 指定 io 线程数，用于性能调优，可选
# web.thread.worker 指定应用工作线程数，用于性能调优，可选
```

当然，您也可以直接通过代码的方式设置各种参数而无需任何配置文件。

~~~java
public static void main(String[] args) {
	WebConfig conf = new WebConfig();
	conf.setPort(9090);
	
	WebLauncher.start(conf);
}
~~~



## 打包

通过下面的方法，可将插件打包为可直接运行的 jar，便于部署、运行： 

 ~~~
mvn -Dmaven.test.skip=true clean compile assembly:single -U
 ~~~

运行

~~~
java -jar nutz-plugins-undertow-1.r.62-SNAPSHOT-jar-with-dependencies.jar
~~~



## 性能调优

Undertow 性能非常优异，在默认参数下即能秒杀tomcat、jetty。

但为了追求更高的性能，undertow 也开放了许多调优的参数供开发者使用。如果你想手动调优，可以先获取到builder，调优后传给插件，启动。

示例代码如下：

~~~java
public static void main(String[] args) {
	Builder builder =  WebLauncher.getDefaultBuilder();
	builder.setBufferSize(1024);
	builder.setIoThreads(10);
	builder.setSocketOption(Options.READ_TIMEOUT, 3*1000);
	
	WebLauncher.start(args, builder);
}
~~~

注：以上参数仅为示例，具体的值需要根据机器的实际情况进行调整。



## 其它

### websocket 的支持

Undertow提供开箱即用的websocket支持，提供了JSR-356的完整实现。要使用此项功能，请将以下片段添加到您的pom.xml中：

~~~
<dependency>
  <groupId>io.undertow</groupId>
  <artifactId>undertow-websockets-jsr</artifactId>
  <version>1.4.12.Final</version>
</dependency>
~~~



### JSP的支持

Undertow可以通过引入 [Jastow](https://github.com/undertow-io/jastow) 来实现 JSP的功能。

Jasper 通过 Servlet 方式实现，因此需要手工获得DeploymentInfo的实例，并通过addServlet方法将其添加到标准的Undertow servlet部署中:

~~~
Builder builder = WebLauncher.getDefaultBuilder();
DeploymentInfo deploy = WebLauncher.getDefaultServletBuilder();
deploy.addServlet(JspServletBuilder.createServlet("Default Jsp Servlet", "*.jsp"));
WebLauncher.start(args, builder, deploy);
~~~



不过更推荐的方式是引用其它高质量的模板引擎。nutz-plugins-views插件提供了freemarker/velocity/thymeleaf/pdf 几种常见的模板引擎支持，您也可以使用beetl模板，它自带对nutz支持。



### 引用自定义filter、servlet

undertow 与传统的web容器不同，不支持web.xml加载filter、servlet，需要通过编程的方式加载。插件已默认加载了nutz mvc的filter。如需加载自定义的filter、servlet，可通过getDefaultServletBuilder()方法获取DeploymentInfo的实例，然后自行织入。

~~~java
Builder builder = WebLauncher.getDefaultBuilder();
DeploymentInfo deploy = WebLauncher.getDefaultServletBuilder();
deploy.addServlet(Servlets.servlet("verifycode", VerifyCodeServlet.class).addInitParam("chars", "23456789abcdefghmnpqrstwxyz").addMapping("/verifycode.jpg"));

WebLauncher.start(args, builder, deploy);
~~~

