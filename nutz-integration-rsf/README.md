nutz-plugins-slog
==================================

简介(可用性:开发中)
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
public class RpcModule extends NutzRsfModule {
    @Override
    public void loadModule(RsfApiBinder apiBinder) throws Throwable {
        //
        apiBinder.bindType(EchoService.class).toProvider(apiBinder.converToProvider(//
                apiBinder.rsfService(EchoService.class)     // 声明服务接口
                        .to(EchoServiceImpl.class)          // 绑定服务实现类(使用 Hasor bean 容器)
                        .register()                         // 发布服务
        ));
        // or
        Provider<EchoService> nutzBean = nutzBean(apiBinder, EchoService.class);
        apiBinder.bindType(EchoService.class).toProvider(apiBinder.converToProvider(//
                apiBinder.rsfService(EchoService.class)     // 声明服务接口
                        .toProvider(nutzBean)               // 绑定服务实现类(使用 nutz Bean 容器)
                        .register()                         // 发布服务
        ));
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