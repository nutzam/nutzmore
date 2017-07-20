nutz-plugins-jedis
==================================

简介(可用性:生产,维护者:wendal)
==================================

深度集成jedis

Redis简介
==================================

官网 http://redis.io 提供了源码下载及文档, 其中windows版是微软自己维护的,地址是 https://github.com/MSOpenTech/redis/releases

Redis普通模式 -- 单机,主从,sentinel,及第三方集群方案(例如codis),均使用原始的redis协议, JedisPool和JedisSentinelPool均继承Pool<Jedis>哦
Redis集群模式 -- Redis Cluster, redis 3.x加入的官方集群方案,使用hash槽的概念在不同节点存储数据, 使用MOVE响应指引客户端跳转到目标服务器, Jedis中的实现类是JedisCluster

jedis奇葩的地方是, JedisPool与JedisCluster均为连接池实现,但后者不继承Pool<Jedis>,所以,使用原生jedis的时候,要么注入JedisPool走普通模式,要么注入JedisCluster走集群模式

背景
==================================

这个插件的代码,最初源于nutzcn的源码. nutzcn即[NutzCN社区](https://github.com/wendal/nutz-book-project), 是nutzbook的衍生项目.

在nutzcn中,最初使用方式是, 注入JedisPool实例, 使用try-with-resources进行资源管理.
后来,发展为通过aop进行自动管理, 即@Aop("redis")方式.
再后来, 为了兼容普通模式和集群模式, 引入JedisAgent,替换原有的JedisPool,实现两种模式的无缝切换.

这里提到的无缝切换是指, 使用同一套代码,在普通模式和集群模式下,都正常使用,不需要大幅修改,只需要改改配置文件.

本插件包含几个核心类
==================================

* JedisAgent -- 它封装了JedisPool和JedisCluster, 可无缝替换现有代码中的JedisPool.
* JedisClusterWrapper -- 将JedisCluster对象封装为Jedis对象.
* RedisInterceptor -- aop拦截器,自动管理Jedis的开启与关闭.

使用方法
-------------------------

本插件提供了ioc加载器(加载源码中的jedis.js),配置方式主要走properties文件

### 在IocBy中引用本插件

```java
@IocBy(args={
	"*js", "ioc/",
	"*anno", "net.wendal.nutzbook",
	"*jedis" // 是的,并没有什么参数
	})
```


### 直接使用jedis()操作(推荐)

通过静态import的RedisInterceptor,配合@Aop注解,通过调用jedis()获取Jedis实例进行操作,无需操心Jedis实例的关闭问题.


```java
import static org.nutz.integration.jedis.RedisInterceptor.jedis;

@Aop("redis") // 必须添加这个注解哦,否则jedis()会抛出空指针
public void addTopic(Topic topic) {
	jedis().set("t:body:"+R.UU32(), Json.toJson(topic,JsonFormat.full()));
	jedis().sadd("t:type:"+topic.getType(), topic.getId());
}
```


### 使用RedisService操作

RedisService类继承Jedis类,其所有方法都经过aop拦截,享有Jedis的遍历,又不需要关心Jedis的close方法,适合不喜欢在自己的方法上标注@Aop的用户.

```java
@Inject RedisService redisService;

public void addTopic(Topic topic) {
	redisService.set("t:body:"+R.UU32(), Json.toJson(topic,JsonFormat.full()));
	redisService.sadd("t:type:"+topic.getType(), topic.getId());
}
```

### 注入JedisAgent

有人可能问,为啥不是注入JedisPool? 原因是,JedisAgent能双模式切换(普通模式和集群模式)

```java
@Inject JedisAgent jedisAgent;

public void addTopic(Topic topic) {
    try (Jedis jedis = jedisAgent.getResouce()) { // 这叫try-with-resources语法, JDK7+适用.
		jedis.set("t:body:"+R.UU32(), Json.toJson(topic,JsonFormat.full()));
		jedis.sadd("t:type:"+topic.getType(), topic.getId());
	}
}

public void addTopic2(Topic topic) {
    Jedis jedis = null;
    try { // JDK6的写法, 长长的try-finally
        jedis = jedisAgent.getResouce();
		jedis.set("t:body:"+R.UU32(), Json.toJson(topic,JsonFormat.full()));
		jedis.sadd("t:type:"+topic.getType(), topic.getId());
	}
	finally {
		Streams.safeClose(jedis);
	}
}
```

注入JedisPool和JedisCluster依然是可用的,虽然不推荐.

```java
// 下面两种对象,不要声明在同一个类哦

// 普通模式
@Inject JedisPool jedisPool;

// 集群模式
@Inject JedisCluster jedisCluster;
```

配置方式
-----------------------------

### 与其他插件类似, 本插件从conf读取redis开头的参数

基本配置

```
redis.host=localhost
redis.port=6379
redis.timeout=2000
#redis.password=wendal.net
redis.database=0

#redis.mode=cluster
```

集群模式,指Redis Cluster

```
redis.host=localhost
redis.port=6379
redis.timeout=2000
redis.mode=cluster
```

如何定制
--------------------------------------

与其他基于ioc的插件一样,同名的bean,优先使用在*您自己*的ioc js定义的类,
所以,jedisPool,jedisCluster等定义均可覆盖哦

请参考插件源码中的jedis.js