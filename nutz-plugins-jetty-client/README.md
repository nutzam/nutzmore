nutz-plugins-jetty-client
==================================

简介(可用性:生产,维护者:wendal)
==================================

为org.nutz.http提供性能更好的实现

用法
==================================

全局使用: 在项目启动时调用一次即可

```
Sender.setFactory(new JettySenderFactory());
```

局部使用

```
public static SenderFactory senderFactory = new JettySenderFactory();

// 原本的调用是这样的
Response resp = Sender.create(req).send();
// 改成
Response resp = senderFactory.create(req).send();
```