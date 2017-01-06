nutz-plugins-websocket
==================================

简介(可用性:生产)
==================================

为websocket提供支持

使用方法
==================================

```java
@ServerEndpoint(value = "/websocket", configurator=NutWsConfigurator.class)
@IocBean(create="init", depose="depose")
public class NutzbookWebsocket extends Endpoint {

	@Inject Dao dao;

	public void init() {}
	public void depose() {}
}
```