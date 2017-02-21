nutz-plugins-slog
==================================

简介(可用性:已完成)
==================================

## Nutz 整合 Hasor 之后 Nutz 哪些方面会有显著提升？

多语言RPC
1. 搭配 RSF 框架之后，Hasor 可以为 Nutz 提供部署完善的RPC服务的能力。
2. RSF 支持 Hprose 框架协议，您可以通过 Hprose 多语言RPC，为 Nutz 异构技术架构提供支持。
```
<dependency>
    <groupId>net.hasor</groupId>
    <artifactId>hasor-rsf</artifactId>
    <version>1.3.0</version>
</dependency>
```

分布式服务
1. RSF 对于服务提供了丰富的控制力，例如：多机房、异地调用、流控、服务路由。
2. 通过 RSF 注册中心可以集中管理您所有RPC服务的：订阅、发布。
```
<dependency>
    <groupId>net.hasor</groupId>
    <artifactId>hasor-registry</artifactId>
    <version>1.3.0</version>
</dependency>
```

用法
==================================

MainModule的IocBy启用该插件

```java
@IocBy(args={"*hasor"})
@IocBy(args={"*hasor","...","...","..."}) // ... 为属性文件或配置文件位置，支持多组
```

使用Hasor or RSF 的入口

```java
@Configuration
public class RpcModule extends NutzModule {
    @Override
    public void loadModule(ApiBinder apiBinder) throws Throwable {
        RsfApiBinder rsfApiBinder = apiBinder.tryCast(RsfApiBinder.class);
        //
        // 服务订阅
        rsfApiBinder.bindType(EchoService.class).toProvider(rsfApiBinder.converToProvider(  // 发布服务到 Hasor 容器中
                        rsfApiBinder.rsfService(EchoService.class).register()               // 注册消费者
                ));
        // 服务发布
        rsfApiBinder.rsfService(EchoService.class)                  // 声明服务接口
            .toProvider(nutzBean(rsfApiBinder, EchoService.class))  // 使用 nutz Bean 中的Bean 作为实现类
            .register();                                            // 发布服务
    }
}
```

@Configuration注解
======================================

* 


NutzRsfModule类
======================================

* 

高级扩展
=======================================

* 参见 Hasor 系列框架。