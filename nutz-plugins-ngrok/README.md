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

- [x] 客户端端
- [x] 服务器端
- [x] 服务器端Redis鉴权
- [x] 基于域名的流量统计
- [ ] 客户端的拦截器机制
- [ ] 客户端web界面

服务器端和客户端均兼容Ngrok原版.

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

服务器端部署需要什么:

* JDK 8最新版
* 若使用redis鉴权, 那么需要redis
* https证书, 合法的,不是自签名的

使用redis与否的区别:

* 非redis模式下,域名是随机6位字母,填写任意access token即可.
* redis模式下, 会执行hget ngrok token 获取域名前缀, 不再是随机域名.

## 你需要一个https证书生成jks文件

使用 crt和key文件, 也就是nginx使用的证书,生成jks的步骤

### 首先, 使用openssl生成p12文件,必须输入密码

```
openssl pkcs12 -export -in 1_wendal.cn_bundle.crt -inkey 2_wendal.cn.key -out wendal.cn.p12
```

### 然后, 使用keytool 生成jks

```
keytool -importkeystore -destkeystore wendal.cn.jks -srckeystore wendal.cn.p12 -srcstoretype pkcs12 -alias 1
```

目标文件是 wendal.cn.jks 即     域名.jks

## 启动NgrokServer

```
java -cp nutz-plugins-ngrok.jar org.nutz.plugins.ngrok.server.NgrokServer -srv_host=wendal.cn -ssl_jks_path=wendal.cn.jks
```

启动日志:

```
2017-3-16 21:59:44.882 INFO [main] Select SystemLog as Nutz.Log implement
2017-3-16 21:59:44.922 DEBUG [main] config key=redis value=true
2017-3-16 21:59:44.975 DEBUG [main] Using 95 castor for Castors
2017-3-16 21:59:44.977 DEBUG [main] NgrokServer start ...
2017-3-16 21:59:44.977 DEBUG [main] try to load Java KeyStore File ...
2017-3-16 21:59:44.985 DEBUG [main] load jks from wendal.cn.jks
2017-3-16 21:59:45.488 DEBUG [main] using default CachedThreadPool
2017-3-16 21:59:45.491 DEBUG [main] using default auth provider
2017-3-16 21:59:45.571 DEBUG [main] start listen srv_port=4443
2017-3-16 21:59:45.574 DEBUG [main] start listen http_port=9080
2017-3-16 21:59:45.575 DEBUG [main] start Contrl Thread...
2017-3-16 21:59:45.575 DEBUG [main] start Http Thread...
```

## 带Redis启动

```
java -cp nutz-plugins-ngrok.jar org.nutz.plugins.ngrok.server.NgrokServer -srv_host=wendal.cn -ssl_jks_path=wendal.cn.jks -redis=true
```

```
2017-3-16 21:59:44.882 INFO [main] Select SystemLog as Nutz.Log implement
2017-3-16 21:59:44.922 DEBUG [main] config key=redis value=true
2017-3-16 21:59:44.975 DEBUG [main] Using 95 castor for Castors
2017-3-16 21:59:44.977 DEBUG [main] NgrokServer start ...
2017-3-16 21:59:44.977 DEBUG [main] try to load Java KeyStore File ...
2017-3-16 21:59:44.985 DEBUG [main] load jks from wendal.cn.jks
2017-3-16 21:59:45.488 DEBUG [main] using default CachedThreadPool
2017-3-16 21:59:45.491 DEBUG [main] using default auth provider
2017-3-16 21:59:45.571 DEBUG [main] start listen srv_port=4443
2017-3-16 21:59:45.574 DEBUG [main] start listen http_port=9080
2017-3-16 21:59:45.575 DEBUG [main] start Contrl Thread...
2017-3-16 21:59:45.575 DEBUG [main] start Http Thread...
```

请在redis下执行

```
hset ngrok aabbccddeeff hello,hi
```

使用 access token=aabbccddeeff 进行登录, 即可拥有 hello.wendal.cn 和 hi.wendal.cn 两个隧道域名