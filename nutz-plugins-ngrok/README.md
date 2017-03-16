用Java实现的Ngrok
==================================

简介(可用性:试用)
==================================

用Java实现的Ngrok的服务器端和客户端.

什么是Ngrok
==================================

ngrok是一个内网穿透隧道, 可以将你的web服务映射到外网的服务器上,从而可以被其他人访问到.

背景
==================================

大学时代的时候,我写过一个类似ngrok的东西,当时技术有限,没能完善就抛弃了.

然后呢, 无意中看到一个github库 [ngrok-java](https://github.com/dosgo/ngrok-java), 
激发我的脑袋: 对哦, 用Java实现一个不就好了嘛?

那,直接用ngrok-java不行吗? 原因是:

* 那项目已经休眠, 2年没更新了
* 代码质量,咳咳..

所以,我昨晚嗑药一样,花了2小时写好了客户端实现, 今天早上又调试优化了一下, 就提交上来了.

功能进度
==================================

- [x]客户端隧道
- [x]服务器端
- []客户端的拦截器机制
- []客户端web界面

客户端用法
===================================

### 编程的,可控的方式

```java
NgrokClient client = new NgrokClient();
client.auth_token = "xxxxx";
client.start();
Thread.sleep(long long time ...);
client.stop();
```

### 命令行方式

```
java -cp nutz-plugins-ngrok.jar org.nutz.plugins.ngrok.client.NgrokClient -auth_token=xxxxxx
```

NgrokClient有大量可以配置的选项,请查阅源码的javadoc注释.

服务器端用法
=================================

