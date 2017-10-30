nutz-plugins-hasor
==================================

简介(可用性:生产,维护者:hasor)
==================================


### Hasor是什么？

&emsp;&emsp; 支持的功能有(J2EE、WebMVC、Restful、RPC、DataQL、IoC、Aop、Xml Reader、Event、J2EE、Form、JDBC、数据库事务)。
&emsp;&emsp; Nutz 整合 Hasor，通过该插件可以在 Nutz 上使用 Hasor 的 RSF、DataQL 等高级服务。下面是摘自 Hasor 官网上的介绍。

#### 特点

- “微内核+插件” 简单、小巧、功能强大、使用简单。
- COC原则的最佳实践，‘零’配置文件。
- 合理的整体架构规划，即是小框架也是大平台。
- 各部分全部独立，按需使用，绝不臃肿。
- 提供 “数据库 + 服务” 整合查询，并提供数据整合能力。
- 体积小，无依赖。

----------
#### 架构
![架构](http://files.hasor.net/uploader/20170609/155318/CC2_403A_3BD5_D581.jpg "架构")

- Core 一款拥有IoC、Aop的模块插件框架
    - 提供一个支持IoC、Aop的Bean容器。
    - 基于 Module + ApiBinder 机制提供统一的插件入口。
    - 特色的 Xml 解析器。让你无需二次开发无需配置，直接读取自定义xml配置文件。
    - 支持模版化配置文件，让您程序打包之后通吃各种环境。
- DB 提供了JDBC操作、事务管理
    - 提供 JDBC 操作接口，并提供简单的 Result -> Object 映射(无需任何配置,包括注解)。
    - 与 Spring 一样，提供七种事务传播属性的控制。
    - 支持多种事务控制方式包括：手动事务控制、注解式声明事务、TransactionTemplate模板事务。
    - 支持多数据源，并且支持多数据源下的事务控制（不支持分布式事务）
- DataQL 提供比 GraphQL 更加灵活好用的服务查询引擎
    - 采用编译执行，拥有飞快的执行速度（内部的几个例子在1W次执行频率下，平均执行时间在1毫秒内）
    - 支持通过 lambda 定义 UDF。
    - 支持查询结果返回一个 UDF。
    - 支持纯 JSON 输入。
    - 支持表达式计算（算数运算、位运算、逻辑运算、比较运算）
    - 支持运算符重载（暂不开放该功能）
    - 支持 if 条件判断。
    - 支持 JSR223
- Web 是一个吸收了百家所长的 Web MVC框架
    - 提供 RESTful 风格的 mvc 开发方式。
    - 提供Form表单验证接口、验证支持场景化。
    - 开放的模版渲染接口，支持各种类型的模版引擎。
    - 内置文件上传组件，无需引入任何jar包。
- RSF 功能堪比淘宝 HSF、dubbo 的分布式 RPC 服务框架
    - 支持容灾、负载均衡、集群
    - 支持通过服务注册中心，支持分布式服务统一治理
    - 支持服务动态发布、动态卸载
    - 支持服务分组、分版本
    - 多种调用方式（点对点、分布式轮训、泛化调用、同步、异步、回调、接口代理）
    - 跨语言：支持通过 Hprose 调用 RSF 的服务。
    - 支持虚拟机房、隐式传参、服务路由、Telnet 等高级功能。
- Pluins Hasor 套件下的第三方插件项目
    - Spring 整合插件（2016-02-16）
    - JFinal 整合插件（2016-11-03）
    - MyBatis3 插件
    - JUnit 插件
    - Freemarker 渲染器插件
    - Json 渲染器插件（支持Json引擎顺序为：fastjson -> gson）
    - 支持与 Nutz 集成（2017-02-21）-> nutz-integration-hasor




RPC用法
==================================

多语言RPC
1. 搭配 RSF 框架之后，Hasor 可以为 Nutz 提供部署完善的RPC服务的能力。
2. RSF 支持 Hprose 框架协议，您可以通过 Hprose 多语言RPC，为 Nutz 异构技术架构提供支持。

```
<dependency>
    <groupId>net.hasor</groupId>
    <artifactId>hasor-rsf</artifactId>
    <version>3.2.1</version>
</dependency>
```

分布式服务
1. RSF 对于服务提供了丰富的控制力，例如：多机房、异地调用、流控、服务路由。
2. 通过 RSF 注册中心可以集中管理您所有RPC服务的：订阅、发布。
```
<dependency>
    <groupId>net.hasor</groupId>
    <artifactId>hasor-registry</artifactId>
    <version>3.2.1</version>
</dependency>
```

本插件通过Ioc容器进行加载,符合nutz标准的插件结构,所以可以直接写入

```java
@IocBy(args={
	"*js", "ioc",
	"*anno", "net.wendal.nutzbook",
	"*hasor"})
```

与其他插件类似, 本插件也依赖名为conf的bean, 引用hasor开头的属性值

```
# 消费者
hasor.config=customer-config.xml
# 提供者
hasor.config=provider-config.xml

# 其余以hasor开头的属性,将作为hasor的环境变量,注入到hasor中
hasor.xxx.xxx=xxxx
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

DataQL用法
==================================
* 请参考 Hasor 官网例子。

@Configuration注解
======================================

* 用于让 Nutz 可以启动时发现 Hasor 的 Module。
* 您也可以通过 Hasor 的配置文件配置 Hasor Module。

NutzModule类
======================================

* Nutz 集成专门定制的 Hasor Module，通过该类提供的 nutzBean 方法，可以在 Hasor 的范围内拿到 Nutz 的 Bean。
* 拿到的 Nutz Bean 是延迟加载的。

高级扩展
=======================================

* 参见 Hasor 系列框架。
* http://www.hasor.net