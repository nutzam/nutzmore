
nutz-plugins-undertow 
==================================

简单、轻量、高性能的嵌入式web服务器插件。



简介(可用性:试用)
==================================

nutz-plugins-undertow 是一个集成了JBOSS Undertow内嵌式高性能WEB服务器插件。Undertow 是红帽公司的开源产品，是 [Wildfly](http://www.oschina.net/p/wildfly) 默认的 Web 服务器。

Undertow 基于java nio开发，非常轻量，将本插件、nutz及所有依赖包全算上，也仅有4.93M，尚不及apache-tomcat-embed包自身的大小。

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

两种方式配置的属性是一致的。下面以配置文件方式进行说明。在源码根目示新建一个配置文件web.properties：

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
# web.thread.io 指定 io 线程数，用于性能调优，可不设置
# web.thread.worker 指定应用工作线程数，用于性能调优，可不设置
```



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



## 注意

undertow 与传统的web容器不同，不支持web.xml加载filter、servlet，需要通过编程的方式加载。插件已默认加载了nutz mvc的filter。如需加载自定义的filter，可通过getDefaultServletBuilder()方法获取DeploymentInfo的实例，然后自行织入。





