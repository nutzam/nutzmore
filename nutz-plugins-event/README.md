
nutz-plugins-event 事件引擎插件
==================================


简介(可用性:试用,维护者:qinerg)
==================================

nutz-plugins-event是一个实现了事件驱动和异步化的nutz框架的插件，方便各模块间解耦。

事件机制为Bean与Bean之间的消息通信提供了支持。当一个Bean任务处理完后，希望另外一个Bean知道并能做相应的处理，这时我们就需要让另外一个Bean监听当前Bean所发送的事件。

组件之间互不依赖，事件生产者只负责向 EventBus 提交事件，不关心这个事件有哪些消费者。事件的消费者也不需关心这个事件是谁产生的，系统会在事件发生时自动派发。


## 特点
+ 轻量(~11k)
+ 快速
+ 支持同步事件和异步事件处理
+ 默认提供了基于 local jvm 和 redis(分布式) 事件引擎




## 用法

本插件提供了单机事件引擎和基于redis的分布式事件引擎。

### 1.单机使用方式

将插件交给ioc容器管理：

```
@IocBy(args={
	"*js", "ioc/",
	"*anno", "com.xxx",
	"*async", // 异步化事件支持，则不需要异步可不声明
	"*org.nutz.plugins.event.EventIocLoader" // 不加参数默认采用jvm事件引擎
})
```

事件监听器需要实现EventListener接口，并声明IocBean注解交由容器管理：

~~~java
@IocBean
public class DemoEventListener implements EventListener {

	private Log log = Logs.get();
	/** 本监听器关注的事件主题 */
	@Override
	public String subscribeTopic() {
		return "userReg";
	}

  	/** 事件具体处理方法 */
	//@Async  //加此注解可实现事件异步处理
	@Override
	public void onEvent(Event e) {
		log.debugf("->into demo event: %s", e.getParam());
	}
}
~~~

默认的事件消费是同步的，用户可以通过在`public void onEvent(Event e)`方法上增加@Async注解的方式实现事件的异步化消费。

异步事件处理完成后，可能通过调用 event.callback(Object result) 方法进行回调通知结果。注意在分布式环境下，这种回调的方式可能无效。

真正的使用：事件生产者发布新的事件：

~~~java
@Inject
private EventBus eventBus;

//用户注册成功
public void regsuccess(User u) {
  dao.insert(u);
  
  Event regEvent = new Event("userReg", u); //创建事件的包裹对象（事件主题, 携带对象）
  eventBus.fireEvent(regEvent); //发布事件
}
~~~

事件发布为广播消息，即所有关注userReg主题的一组监听器都会收到消息。

利用这种机制，开发者可以将注册完成后的事项，如发欢迎邮件、发站内信、增加用户积分、默认关注等功能通过不同的消费者实现，避免相互之间的代码耦合。



### 2.分布式的使用方式

分布式事件分发，可增加事件处理容量，另外当系统意外崩溃时已经发出的消息不会丢失，从而提高了系统的稳定性。该实现基于redis，用户可参考EventBus接口扩展出基于MQ等方式的事件中心。

分布式事件中心依赖`nutz-integration-jedis`，请引用该插件的maven包，并做适当配置。

将插件交给ioc容器管理：

```
@IocBy(args={
	"*js", "ioc/",
	"*anno", "com.xxx",
	"*jedis", // 引用jedis插件 
	"*org.nutz.plugins.event.EventIocLoader", "redis" // 增加参数启用redis事件引擎
})
```

其它代码同单机版一样。

项目如果启动了多个实例，事件的实际消费者会随机产生于这些实例之上。



