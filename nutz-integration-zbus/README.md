nutz-integration-zbus
==================================

简介(可用性:试用,维护者:wendal)
==================================

深度集成zbus,提供mq,rpc支持

由于zbus api大改,本插件无法同时兼容新老zbus

1.r.59开始仅支持zbus 7.x+

添加maven依赖项
==================================

```xml
		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>nutz-integration-zbus</artifactId>
			<version>1.r.60</version>
		</dependency>
```

在MainModule中添加ZBusIocLoader
===================================

```java
@IocBy(args={"*js", "ioc/",
			 "*anno", "net.wendal.nutzbook",
			 "*zbus", "net.wendal.nutzbook"})
```

注意其可选参数,代表需要扫描的ZBus代理接口

添加配置文件custom/zbus.properties
===================================

配置文件用"conf"这个bean统一加载, 参考 http://nutzbook.wendal.net/dev_prepare/better_dao_js.html

```
## 共用配置(mq,rpc均需要!!)
zbus.serverAddr=127.0.0.1:15555
zbus.mq.name=nutzbook

## 内嵌zbus服务器端的配置
## 生产环境下请使用独立的zbus注册服务!!!
zbus.server.embed.enable=true
zbus.serverPort=15555
zbus.serverHost=127.0.0.1
#zbus.trackServerList=
#zbus.thriftServer=
zbus.selectorCount=1
zbus.executorCount=64
zbus.verbose=false
zbus.storePath=mq

## RPC服务端配置(仅rpc需要)
zbus.rpc.service.enable=true
zbus.rpc.service.consumerCount=2
```

在MainSetup.init方法内启动
===================================

```java
		// 获取配置对象
		PropertiesProxy conf = ioc.get(PropertiesProxy.class, "conf");

		// 启动zbus######################
		// 启动内置zbus服务器,通常不需要!!!
		//if (conf.getBoolean("zbus.server.embed.enable", false)) {
		//	ioc.get(MqServer.class);
		//}
		// 启动RPC服务端,按需选用
		if (conf.getBoolean("zbus.rpc.service.enable", false)) {
			RpcProcessor rpcProcessor = ioc.get(RpcProcessor.class);
			// 通过buildServices扫描所有标准了@ZBusService的类
			ZBusFactory.buildServices(rpcProcessor, ioc, getClass().getPackage().getName());
			ioc.get(Service.class, "rpcService"); // 注意, Service与服务器连接是异步操作
		}
		// 启动 生产者/消费者(即MQ服务),按需选用
		ZBusFactory zbus = ioc.get(ZBusFactory.class, "zbus");
		zbus.init(getClass().getPackage().getName());
```

RPC用法
===================================

ZBus的RPC的3个角色:

* 代理接口 -- 调用者与服务提供者的通信渠道.调用双方必须使用完全一样的接口哦
* 服务提供者 -- 提供代理接口的具体实现
* 调用者 -- 只认识代理接口的一个普通类(通过注入得到代理接口的实例)

### 代理接口 @ZBusInvoker

RPC的两方,是通过接口通信的,而这个接口,必须声明 @ZBusInvoker

```java
@ZBusInvoker
public interface EmailService {
    boolean send(String to, String subject, String html)
}
```

定义上述接口后, NutIoc会生成一个虚拟的bean,名字叫 "emailService",即类名首字母小写. ZBusInvoker 有value属性可以自定义名称.

### 服务提供者 @ZBusService

服务提供者,必须是一个类并实现代理接口.

```java
@ZBusService
@IocBean
public class EmailServiceImpl implements EmailService {
    public boolean send(String to, String subject, String html) {
        return true;
    }
}
```

### 调用者, 无需添加注解

按普通注入方式,注入对应的bean. 对调用者来说是透明的,没有任何zbus的痕迹

```java
@IocBean
public class YvrService {

    @Inject
    protect EmailService emailService; // 直接注入
    
    @Inject 
    protect Dao dao;
    
    public void addTopic(Topic topic) {
    	dao.insert(topic);
    	emailService.send("vt400@qq.com", "有新帖子", "https://nutz.cn/yvr/t/" + topic.getId());
    }
}
```


## MQ用法

### 订阅者 @ZBusConsumer

可以标注在方法上(推荐):

```java
public class YvrService {
	@ZBusConsumer(mq="topic:update")
	public void topicUpdate(Message msg, Consumer consumer) {
		// ...
	}
}
```

可以标注在类上:

```
@ZBusConsumer(mq="topic")
@IocBean
public class YvrService implements ConsumerHandler {
   // ...
}
```

### 发布者

```
@IocBean
public class YvrSuper {

	@Inject("java:$zbus.getProducer('topic:update')")
	protect ZBusProducer topicUpdateMq;

	// 然后 就可以操作zbus的ZBusProducer实例了
}
```