nutz-integration-lettuce
==================================

简介(可用性:生产,维护者:wendal)
==================================

深度集成lettuce

lettuce简介
==================================

官网 http://redis.io 提供了源码下载及文档, 其中windows版是微软自己维护的,地址是 https://github.com/MSOpenTech/redis/releases

这是howe要求添加的插件, 行为与nutz-integration-jedis类似

本插件包含几个核心类
==================================

* LettuceIocLoader -- 兼容1.r.62以上的nutz
* LettuceInterceptor -- aop拦截器,自动管理Jedis的开启与关闭.

使用方法
-------------------------

本插件提供了ioc加载器,配置方式主要走properties文件

### 在IocBy中引用本插件

```java
@IocBy(args={
	"*js", "ioc/",
	"*anno", "net.wendal.nutzbook",
	"*lettuce" // 是的,并没有什么参数
	})
```


### 直接使用lettuce()操作(推荐)

通过静态import的LettuceInterceptor,配合@Aop注解,通过调用lettuce()获取lettuce连接实例进行操作,无需操心lettuce连接的的关闭问题.


```java
import static org.nutz.integration.lettuce.LettuceInterceptor.lettuce;

@Aop("lettuce") // 必须添加这个注解哦,否则lettuce()会抛出空指针
public void addTopic(Topic topic) {
	RedisCommands<String, String> sync = lettuce().sync();
	System.out.println("PING: " + sync.ping());
}
```