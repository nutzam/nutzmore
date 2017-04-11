nutz-plugins-websocket
==================================

简介(可用性:生产)
==================================

为websocket提供完整支持

使用WebSocket最低要求:

* Tomcat 8 或 Jetty 9.2+
* JDK7+

低于该版本的就别想搞websocket了

使用方法
==================================

### 服务器端入口类

```java
@ServerEndpoint(value = "/websocket", configurator=NutWsConfigurator.class)
@IocBean
public class MyWebsocket extends AbstractWsEndpoint {
}
```

### 页面端js示例

假设是jsp页面, 其中的base是项目的Context Path, home是房间的名称

```js
        var ws;
		var WS_URL = window.location.host + ${base} + "/websocket"
		if (location.protocol == 'http:') {
			ws = new WebSocket("ws://"+WS_URL);
		} else { // 如果页面是https,那么必须走wss协议
			ws = new WebSocket("wss://"+WS_URL);
		}
		ws.onmessage = function(event) {
		    var re = JSON.parse(event.data);
		    if (re.action == "notify") {
		    	// 弹个浏览器通知?
		    } else if (re.action == "layer") {
		    	// 弹个层?
		    	layer.alert(re.msg);
		    }
		};
		ws.onopen = function(event) {
		    // 加入home房间
			ws.send(JSON.stringify({room:'home',"action":"join"}));
		};
```

### 从页面发消息给服务器:

```js
ws.send(JSON.stringify({room:'房间名称',"action":"join"}));
```

### 从服务器发消息给房间

```java
通过ioc注入上述的MyWebsocket
@Inject
protected MyWebsocket myWebsocket;

public void sayhi(String room) {
    myWebsocket.each(room, new Each<Session>() {
    	public void invoke(int index, Session ele, int length) {
                myWebsocket.sendJson(ele.getId(), new NutMap("action", "layer").setv("msg", "hi"));
            }
    });
}
```

### 从服务器发消息给指定的WebSocket会话

```java
通过ioc注入上述的MyWebsocket
@Inject
protect MyWebsocket myWebsocket;

public void sayhi(String wsid) {
    myWebsocket.sendJson(wsid, new NutMap("action", "layer").setv("msg", "hi"));
}
```

另外发送文本,二进制数据的异步或同步方法,请查阅AbstractWsEndpoint的javadoc

# 如何定制

## 定制 消息处理类

即WsHandler的实例,覆盖AbstractWsEndpoint的createHandler方法就可以了.

需要注意的是, WsHandler的实例,必须实现MessageHandler.Whole或MessageHandler.Partial接口,两个接口二选一.

## 定制房间存储

我们内置了两个房间存储实现, 基于内存的MemoryRoomProvider和基于redis的JedisRoomProvider

默认是MemoryRoomProvider,适合中小型应用,单机.

AbstractWsEndpoint有一个属性叫 roomProvider, 把它设置成需要的实例就可以了.

## 故障排除

* TODO
