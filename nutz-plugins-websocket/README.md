nutz-plugins-websocket
==================================

简介(可用性:生产,维护者:wendal)
==================================

为websocket提供完整支持

使用WebSocket最低要求:

* Tomcat 8 或 Jetty 9.2+
* JDK7+

低于该版本的就别想搞websocket了

## nutz到底给websocket封装了啥?

* 封装了configurator,提供ioc注入服务和HttpSession关联支持
* 接管了OnOpen/OnMessage/OnError/OnClose方法,让使用者专注于业务逻辑
* 提供了"房间"这一概念的实现. 房间指一群WebSocket会话.

### 有什么需要提前注意的地方吗?

* WebSocket Session与HttpSession是独立的,互不依赖.
* 对一个服务来说,同一个浏览器实例一般只有一个HttpSession,但WebSocket Session可以有无数个.
* 除WebSocket的OnOpen阶段, HttpServletRequest/HttpServletResponse都是不可用的
* 虽然WebSocket支持传字节数据,但绝大多数情况下是文本

### Nutz集成Websocket必须用这个插件吗?

不是, 正如所有nutzmore项目那样, nutz都给予完全的自由,你可以完全无视这些集成项目,自己写一套的.

如果使用原生WebSocketApi已经非常顺畅,而感觉本插件非常繁琐,那就继续用原生嘛,不强求的,也强求不来嘛.

### Maven依赖配置

```xml
		<dependency>
			<groupId>javax.websocket</groupId>
			<artifactId>javax.websocket-api</artifactId>
			<version>1.1</version>
			<scope>provided</scope>
		</dependency>
		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>nutz-plugins-websocket</artifactId>
			<version>1.r.62</version>
		</dependency>
```

使用方法
==================================

### 服务器端入口类

WebSocket的入口类叫 "Endpoint", 虽然可以通过api手工注册,但原生注解声明一下也是很方便的.

```java
// ServerEndpoint是websocket的必备注解, value是映射路径, configurator是配置类.
@ServerEndpoint(value = "/websocket", configurator=NutWsConfigurator.class)
@IocBean // 使用NutWsConfigurator的必备条件
public class MyWebsocket extends AbstractWsEndpoint {
    // 并不需要你马上实现任何方法,它也马上能工作
}
```

**特别提醒: 已知限制, Endpoint类不能使用@Aop或者aop相关的注解(如@Async/@SLog)**

### 页面端js示例

假设是jsp页面, 其中的base是项目的Context Path, home是房间的名称

```js
// 首先,需要创建一个WebSocket连接
var ws;
var WS_URL = window.location.host + ${base} + "/websocket"
 // 如果页面是https,那么必须走wss协议, 否则走ws协议
if (location.protocol == 'http:') {
    ws = new WebSocket("ws://"+WS_URL);
} else {
    ws = new WebSocket("wss://"+WS_URL);
}
// 连接成功后,会触发onopen回调
ws.onopen = function(event) {
    console.log("websocket onopen ...");
    // 加入home房间
    ws.send(JSON.stringify({room:'home',"action":"join"}));
};
// 收到服务器发来的信息时触发的回调
ws.onmessage = function(event) {
    console.log("websocket onmessage", event.data);
    var re = JSON.parse(event.data);
    if (re.action == "notify") {
        // 弹个浏览器通知
    } else if (re.action == "msg") {
        // 插入到聊天记录中
    }
};

// 定时发个空消息,避免服务器断开连接
function ws_ping() {
	if (ws) {
		ws.send("{}"); // TODO 断线重连.
	}
}
setInterval("ws_ping()", 25000); // 25秒一次就可以了
```

### 默认的信息处理器WsHandler

AbstractWsEndpoint的默认的WsHandler实现是SimpleWsHandler, 足够满足前端写个简单chat应用.

其中action名字均对应SimpleWsHandler中的同名方法, 例如 action:"join" 对应的是 join(NutMap req)方法

```js
// 加入指定房间
ws.send(JSON.stringify({room:'房间名称',"action":"join"}));
// 离开指定房间
ws.send(JSON.stringify({room:'房间名称',"action":"left"}));
// 发送消息到指定房间
ws.send(JSON.stringify({room:'房间名称',"action":"msg2room", "msg" : "大家好!!"}));
// 设置昵称
ws.send(JSON.stringify({"action":"nickname", "nickname" : "wendal"}));
```



### 从服务器主动发消息给房间

主动是指,在websocket的WsHandler之外,由业务逻辑决定推送通知. 例如新任务提醒,在任务添加完成后,向指定房间发送通知.


```java
// 在Service或Module中,通过ioc注入上述的MyWebsocket
@Inject
protected MyWebsocket myWebsocket;

// 按业务需要,调用myWebsocket提供的各种api
public void send_job_notify(String room, final String from) {
    // 通过each方法变量房间内的会话
    myWsHandler.each(room, new Each<Session>() {
    	public void invoke(int index, Session ele, int length) {
    	        // 逐个会话发送消息
                myWebsocket.sendJson(ele.getId(), new NutMap("action", "layer").setv("notify", "你有新的待办事宜,请查看收件箱 from=" + from));
            }
    });
}
```

另外发送文本,二进制数据的异步或同步方法,请查阅AbstractWsEndpoint的javadoc

## 如何扩展

### 扩展或实现WsHandler(简单版)

通常我会建议你继承SimpleWsHandler,添加自定义方法

```java
public class MySimpleWsHandler extends SimpleWsHandler {
    public MySimpleWsHandler() {
        super(""); // 覆盖默认前缀
    }
    public void sayhi(NutMap req) { // 对应js端的action名称,方法参数必须是NutMap哦
        String name = req.getString("name");// 可以拿到页面发过来的任意内容
        NutMap resp = new NutMap("action", "notify"); // 响应的内容完全由你决定,推荐用{action:"xxx", ....}
        resp.setv("msg", "hi, " + name);
        endpoint.sendJson(session.getId(), resp); // 通过endpoint可以发生给任何你想发生的对象, session就是当前WebSocket的会话.
    }
}

// 让WsEndpoint使用你自己的WsHandler
@ServerEndpoint(value = "/websocket", configurator=NutWsConfigurator.class)
@IocBean
public class MyWebsocket extends AbstractWsEndpoint {
    public WsHandler createHandler(Session session, EndpointConfig config) {
        return new MySimpleWsHandler(); // 是的,返回你自己的实现类就可以了,需要每次新建哦
    }
}
```

页面端就可以发自定义消息了

```js
ws.send(JSON.stringify({"action":"sayhi", "nickname" : "wendal"}));
```

发送完成后, 后端的sayhi方法应该会接受到信息,然后页面端的onmessage方法马上收到响应

### 如何扩展或实现WsHandler(深入版)

如果你完全不需要SimpleWsHandler的默认方法,可以考虑继承AbstractWsHandler, 它相当于一个无action方法的空壳

如果你希望更深入集成,可以直接实现WsHandler接口,完全按你的需求来做.

### 从服务器发消息给指定的WebSocket会话

这算是高级用法哦.

默认情况下,你是不能通过HttpSession找到其对应的WebSocket的, 因为他们互不依赖.

但是,我们依然提供一个途径供你扩展, 请仔细观察一下WsHandler接口, 它有3个值得注意的方法

```java
    void init(); // 其他set方法调用完成后的初始化回调
    void setSession(Session session); // WebSocket会话
    void setHttpSession(HttpSession httpSession); // HttpSession会话
```

所以, 自定义WsHandler实现类的时候,覆盖init方法

```java
public void init() {
    super.init(); // 必须调用超类的init,除非直接实现WsHandler接口
    if (httpSession != null)
        httpSession.setAttribute("wsid", session.getId()); // 其他业务代码只需要从HttpSession取出wsid,即可调用AbstractWsEndpoint的api发送消息
}
```

或者在任意自定义action方法内调用上述逻辑,也能把HttpSession与WebSocket Session关联起来.

局限性: 一个HttpSession可能有N个WebSocket Session, 上述逻辑会导致wsid覆盖的问题, 也许你想想到更好的存储方式,欢迎与我们交流.

## 定制房间存储

我们内置了两个房间存储实现, 基于内存的MemoryRoomProvider和基于redis的JedisRoomProvider

默认是MemoryRoomProvider,适合中小型应用,单机.

AbstractWsEndpoint有一个属性叫 roomProvider, 自定义Endpoint的实例,把它设置成你需要的实例就可以了.

## 故障排除

* 404 -- 如果是纯Tomcat/Jetty,版本也够高的话, 通常是路径错误.
* 404 -- 在Nginx之后, 需要以下特别配置

```txt
proxy_read_timeout 60m;
proxy_set_header Upgrade $http_upgrade;
proxy_set_header Connection "upgrade";
```

* 500 -- 通常configurator或OnOpen抛异常了,而且没catch
