集成rabbitmq
==================================

简介(可用性:试用,维护者:wendal)
==================================

集成rabbitmq很复杂吗?核心是拿到Channel对象嘛.

基本用法
===================================

加jar之类的不说了, 你我都懂的. 

与其他集成类似, rabbitmq也是走ioc集成, 也是依赖conf进行配置.

```java
@IocBy(args={
		"*js", "ioc/",
		"*anno", "net.wendal.nutzbook",
		"*rabbitmq"

})
```

### 添加rabbitmq.properties, 确保在conf的扫描路径内.

```
rabbitmq.host=127.0.0.1
```

### 使用实例

```java
package org.nutz.integration.rabbitmq;

import static org.nutz.integration.rabbitmq.aop.RabbitmqMethodInterceptor.*;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean // 必须是Ioc对象, 这是@Aop注解的要求.
public class RabbitTestService {
    
    @Aop("rabbitmq") // 会自动管理Connection/Channel的开启和关闭.
    public void publish(String routingKey, byte[] body) throws Exception {
        channel().basicPublish("", routingKey, null, body);
    }
}
```