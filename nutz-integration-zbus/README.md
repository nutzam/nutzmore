nutz-integration-zbus
==================================

简介(可用性:试用)
==================================

深度集成zbus,提供mq,rpc支持

用法(nutz 1.r.57+)
===================================

```java
@IocBy(args={"*js", "ioc/",
			 "*anno", "net.wendal.nutzbook",
			 "*zbus"})
```