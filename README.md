# Nutz的插件与集成库

[![Build Status](https://travis-ci.org/nutzam/nutzmore.png?branch=master)](https://travis-ci.org/nutzam/nutzmore)
[![Circle CI](https://circleci.com/gh/nutzam/nutzmore/tree/master.svg?style=svg)](https://circleci.com/gh/nutzam/nutzmore/tree/master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutzmore/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutzmore/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

### 各种插件和集成的集合

每个插件都有自己的文件夹,均为maven module, 请按需获取.

```xml
		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>填nutz插件名</artifactId>
			<version>1.r.67</version>
		</dependency>
```

# 快照版地址

https://jfrog.nutz.cn/artifactory/snapshots/org/nutz/

```xml
	<repositories>
		<repository>
			<id>nutzcn-snapshots</id>
			<url>https://jfrog.nutz.cn/artifactory/snapshots</url>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
	</repositories>
	<dependencies>
		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>填nutz插件名</artifactId>
			<version>填版本号-SNAPSHOT</version>
		</dependency>
		<!-- 其他依赖 -->
	</dependencies>
```

## 各插件可用性

| 插件名称                                     | 版本                                       | 简介                                       | 可靠性    |   维护者 |
| ---------------------------------------- | ---------------------------------------- | ---------------------------------------- | ------ |-------|
| [integration-activiti](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-activiti) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-activiti/badge.svg) | 提供Activiti相关的Ioc配置 | 试用 | wendal |
| [integration-authz](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-authz) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-authz/badge.svg) | 集成jCasbin的访问控制、权限管理机制 | 试用 | hsluoyz |
| [integration-autoloadcache](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-autoloadcache) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-autoloadcache/badge.svg) | 深度集成AutoLoadCache | **生产** | Rekoe |
| [integration-bex5](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-bex5) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-bex5/badge.svg) | Bex5与Nutz集成 | 试用 | ecoolper |
| [integration-cxf](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-cxf) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-cxf/badge.svg) | 轻度集成Cxf(WebService) | 试用 | wendal |
| [integration-dubbo](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-dubbo) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-dubbo/badge.svg) | 兼容原生dubbo配置文件 | **生产** | wendal |
| [integration-dwr](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-dwr) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-dwr/badge.svg) | 轻度集成dwr | 试用 | wendal |
| [integration-grpc](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-grpc) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-grpc/badge.svg) | 封装grpc | 开发中 | wendal |
| [integration-hasor](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-hasor) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-hasor/badge.svg) | Nutz 深度整合 Hasor/rsf | **生产** | hasor |
| [integration-hessian](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-hessian) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-hessian/badge.svg) | 提供hessian的Mvc适配器 | **生产** | Rekoe |
| [integration-jcache](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-jcache) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-jcache/badge.svg) | 集成Jcache的方法级注释 | 废弃 |  |
| [integration-jedis](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-jedis) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-jedis/badge.svg) | 深度集成jedis | **生产** | wendal |
| [integration-jedisque](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-jedisque) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-jedisque/badge.svg) | 简单集成jedisque | 试用 | wendal |
| [integration-jsch](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-jsch) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-jsch/badge.svg) | 简单演示jsch的端口映射 | 试用 |  |
| [integration-jsf](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-jsf) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-jsf/badge.svg) | 提供JSF集成所需要的EL解析器 | 废弃 | wendal |
| [integration-json4excel](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-json4excel) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-json4excel/badge.svg) | [Apache POI](https://poi.apache.org/)项目的封装，简化了一些常见的操作 | **生产** | pangwu86 |
| [integration-jsr303](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-jsr303) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-jsr303/badge.svg) | 深度集成jsr303的校验机制 | 试用 | wendal |
| [integration-neo4j](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-neo4j) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-neo4j/badge.svg) | 集成neo4j | 试用 | wendal |
| [integration-quartz](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-quartz) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-quartz/badge.svg) | 集成Quartz(计划任务/定时任务)的不二选择 | **生产** | wendal |
| [integration-rabbitmq](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-rabbitmq) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-rabbitmq/badge.svg) | 集成rabbitmq很复杂吗?核心是拿到Channel对象嘛. | 试用 | wendal |
| [integration-shiro](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-shiro) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-shiro/badge.svg) | 集成Shiro的登陆,鉴权,和Session机制 | **生产** | wendal |
| [integration-spring](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-spring) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-spring/badge.svg) | Spring与Nutz集成所需要的一切 | **生产** | wendal |
| [integration-struts2](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-struts2) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-struts2/badge.svg) | 替换struts2的Ioc容器为NutIoc | 废弃 | wendal |
| [integration-swagger](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-swagger) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-swagger/badge.svg) | 深度集成Swagger | 试用 | wendal |
| [integration-zbus](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-zbus) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-zbus/badge.svg) | 深度集成zbus,提供mq,rpc支持 | 试用 | wendal |
| [integration-zookeeper](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-zookeeper) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-zookeeper/badge.svg) | 待编写 | 开发中 |  |
| [maven-quickstart](https://github.com/nutzam/nutzmore/tree/master/nutz-maven-quickstart) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-maven-quickstart/badge.svg) | 待编写 | 开发中 | wendal |
| [plugins-apidoc](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-apidoc) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-apidoc/badge.svg) | API文档生成及调试 | **生产** | wendal |
| [plugins-cache](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-cache) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-cache/badge.svg) | Shiro的CacheManager实现 | **生产** | wendal |
| [plugins-daocache](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-daocache) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-daocache/badge.svg) | 为NutDao提供缓存支持,SQL级别的缓存 | **生产** | wendal |
| [plugins-daomapping](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-daomapping) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-daomapping/badge.svg) | Dao接口无缝生成 | 试用 | wendal |
| [plugins-dict](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-dict) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-dict/badge.svg) | 针对java常量的全局字典生成 | 试用 | [邓华锋](http://dhf.ink) |
|[plugins-event](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-event) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-event/badge.svg) | 事件驱动和异步化插件,方便各模块间解耦 | 试用 | qinerg |
| [plugins-hotplug](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-hotplug) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-hotplug/badge.svg) | 定义一套基础架构,实现可插拔系统 | **生产** | wendal |
| [plugins-iocloader](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-iocloader) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-iocloader/badge.svg) | 自定义IocLoader，及任务线程环境下使用ioc方式| 试用 | wendal、[邓华锋](http://dhf.ink)  |
| [plugins-jqgrid](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-jqgrid) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-jqgrid/badge.svg) | 封装[jqGrid](http://http://blog.mn886.net/jqGrid/) dao层操作通用使用方法 | **生产** | [邓华锋](http://dhf.ink) |
|[plugins-jsonrpc](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-jsonrpc) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-jsonrpc/badge.svg) | 完整实现jsonrpc, 用Mapper方式 | 废弃 | wendal |
| [plugins-mock](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-mock) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-mock/badge.svg) | 提供单元测试所需要的一切东西 | 试用 | wendal |
| [plugins-multiview](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-multiview/badge.svg) | 集合N种模板引擎,可配置性强 | **生产** | [邓华锋](http://dhf.ink) |
| [plugins-mongodb](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-mongodb) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-mongodb/badge.svg) | Mongodb 薄封装 | **生产** | wendal |
| [plugins-ngrok](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-ngrok) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-ngrok/badge.svg) | 用Java实现的Ngrok的服务器端和客户端. | 试用 | wendal |
| [plugins-nop](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-nop) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-nop/badge.svg) | NUTZ OPEN PLATFORM | **生产** | 王贵源 |
| [plugins-oauth2-server](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-oauth2-server) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-oauth2-server/badge.svg) | 使用Apache Oltu 搭建Oauth2 Server及Client开放授权 | **生产** | Rekoe |
| [plugins-profiler](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-profiler) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-profiler/badge.svg) | 性能监控 | 开发中 | wendal |
| [plugins-protobuf](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-protobuf) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-protobuf/badge.svg) | 提供protobuf双向通信所需要的适配器和View | **生产** | Rekoe |
| [plugins-qrcode](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-qrcode) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-qrcode/badge.svg) | 生成 QRCode,基于 [zxing](http://code.google.com/p/zxing/) | **生产** | 冬日温泉 |
| [plugins-secken](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-secken) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-secken/badge.svg) | 完整实现洋葱登陆协议 | 废弃 | wendal |
| [plugins-sfntly](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-sfntly) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-sfntly/badge.svg) | sfntly的fork版本,修正错误并添加可编程调用 | **生产** | wendal |
| [plugins-sigar](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-sigar) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-sigar/badge.svg) | 深度集成sigar | 试用 | 王贵源 |
| [plugins-slog](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-slog) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-slog/badge.svg) | 注解式系统日志 | **生产** | wendal |
| [plugins-spring-boot-starter](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-spring-boot-starter) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-spring-boot-starter/badge.svg) | spring-boot 环境下使用 nutz-dao 和 nutzjson  | **生产** | 王贵源 |
| [plugins-sqltpl](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-sqltpl) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-sqltpl/badge.svg) | 支持多种模板引擎 | 试用 | wendal |
| [plugins-sqlmanager](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-sqlmanager) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-sqlmanager/badge.svg) | 各式各样的SqlManaget实现 | 试用 | wendal等 |
| [plugins-thrift-netty](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-thrift-netty) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-thrift-netty/badge.svg) | 深度集成thrift-netty | 试用 | Rekoe |
| [plugins-undertow](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-undertow) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-undertow/badge.svg) | 集成JBOSS Undertow高性能web服务器插件 | 试用 | qinerg |
| [plugins-validation](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-validation) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-validation/badge.svg) | 独立,小巧且够用的校验库 | 试用 | wendal |
| [plugins-views](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-views) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-views/badge.svg) | freemarker/velocity/thymeleaf/pdf 视图插件 | **生产** |  |
| [plugins-webqq](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-webqq) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-webqq/badge.svg) | webqq集成 | 废弃 |  |
| [plugins-websocket](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-websocket) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-websocket/badge.svg) | 为websocket提供完整支持 | **生产** | wendal |
| [plugins-wkcache](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-wkcache) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-wkcache/badge.svg) | 基于Redis实现的方法缓存 | **生产** | 大鲨鱼 |
| [plugins-xmlentitymaker](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-xmlentitymaker) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-xmlentitymaker/badge.svg) | 使用xml定义实体,替换原生的注解方式 | 试用 | wendal |
| [plugins-zcron](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-zcron) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-zcron/badge.svg) | 定期运行的表达式 | 试用 | zozoh |
| [plugins-zdoc](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-zdoc) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-zdoc/badge.svg) | 写文档也可以很轻松 | 试用 | zozoh |
| [integration-nettice](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-nettice) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-nettice/badge.svg) | Netty的Http Router | 试用 | Rekoe |
| [plugins-fiddler](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-fiddler) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-fiddler/badge.svg) | 抓包工具 | 试用 | Rekoe |
