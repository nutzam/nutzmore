nutz-plugins-jedis
==================================

简介(可用性:试用)
==================================

轻度集成jedis

使用方法
-------------------------

在IocBy中引用本插件

```java
@IocBy(args={
	"*js", "ioc/",
	"*anno", "net.wendal.nutzbook",
	"*jedis"
	})
```

使用RedisService操作


```java
@Inject RedisService redisService;
```

直接使用jedis()操作


```java
import static org.nutz.plugin.jedis.RedisInterceptor.jedis;

@Aop("redis")
public void addTopic(Topic topic) {
	jedis().set("topic:"+R.UU32(), Json.toJson(topic,JsonFormat.full()));
}
```

配置方式
-----------------------------

与其他插件类似, 本插件从conf读取redis开头的参数

```
redis.host=localhost
redis.port=6379
redis.timeout=2000
#redis.password=wendal.net
redis.database=0
```

若需要使用redis主从或集群功能, 可覆盖默认的jedisPool,重新定义一个ioc的bean.