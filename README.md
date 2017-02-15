# nutzmore Nutz的插件与扩展

[![Build Status](https://travis-ci.org/nutzam/nutzmore.png?branch=master)](https://travis-ci.org/nutzam/nutzmore)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutzmore/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutzmore/)
[![License](https://img.shields.io/badge/license-Apache%202-4EB1BA.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

### 各种官方插件的集合

每个插件都有自己的文件夹,均为maven module, 请按需获取.

```xml

		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>填nutz插件名</artifactId>
			<version>1.r.60.r2</version>
		</dependency>
```

或者直接上聚合jar, 包含大部分常用插件和第三方集成

```xml

		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>nutzmore-aggregate</artifactId>
			<version>1.r.60.r2</version>
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

| 插件名称 | 版本 | 简介 | 可靠性 |
| ------| ------ | ------ | ------ |
| [nutz-integration-activiti](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-activiti) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-activiti/badge.svg) | 提供Activiti相关的Ioc配置 | 开发中 |
| [nutz-integration-autoloadcache](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-autoloadcache) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-autoloadcache/badge.svg) | 深度集成AutoLoadCache | **生产** |
| [nutz-integration-dubbo](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-dubbo) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-dubbo/badge.svg) | 兼容原生dubbo配置文件 | 试用 |
| [nutz-integration-dwr](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-dwr) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-dwr/badge.svg) | 轻度集成dwr | 试用 |
| [nutz-integration-hessian](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-hessian) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-hessian/badge.svg) | 提供hessian的Mvc适配器 | **生产** |
| [nutz-integration-jcache](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-jcache) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-jcache/badge.svg) | 集成Jcache的方法级注释 | 试用 |
| [nutz-integration-jedis](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-jedis) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-jedis/badge.svg) | 深度集成jedis | 试用 |
| [nutz-integration-jsch](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-jsch) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-jsch/badge.svg) | 简单演示jsch的端口映射 | 试用 |
| [nutz-integration-jsf](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-jsf) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-jsf/badge.svg) | 提供JSF集成所需要的EL解析器 | 试用 |
| [nutz-integration-jsr303](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-jsr303) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-jsr303/badge.svg) | 深度集成jsr303的校验机制 | 试用 |
| [nutz-integration-quartz](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-quartz) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-quartz/badge.svg) | 集成Quartz(计划任务/定时任务)的不二选择 | **生产** |
| [nutz-integration-rsf](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-rsf) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-rsf/badge.svg) | Nutz与Rsf无缝集成 | 开发中 |
| [nutz-integration-shiro](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-shiro) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-shiro/badge.svg) | 集成Shiro的登陆,鉴权,和Session机制 | **生产** |
| [nutz-integration-spring](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-spring) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-spring/badge.svg) | Spring与Nutz集成所需要的一切 | **生产** |
| [nutz-integration-struts2](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-struts2) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-struts2/badge.svg) | 替换struts2的Ioc容器为NutIoc | 试用 |
| [nutz-integration-zbus](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-zbus) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-integration-zbus/badge.svg) | 深度集成zbus,提供mq,rpc支持 | 试用 |
| [nutz-maven-quickstart](https://github.com/nutzam/nutzmore/tree/master/nutz-maven-quickstart) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-maven-quickstart/badge.svg) | 待编写 | 开发中 |
| [nutz-plugins-apidoc](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-apidoc) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-apidoc/badge.svg) | API文档生成及调试 | 试用 |
| [nutz-plugins-cache](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-cache) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-cache/badge.svg) | 待编写 | 开发中 |
| [nutz-plugins-daocache](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-daocache) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-daocache/badge.svg) | 为NutDao提供缓存支持,SQL级别的缓存 | **生产** |
| [nutz-plugins-daomapping](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-daomapping) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-daomapping/badge.svg) | 待编写 | 开发中 |
| [nutz-plugins-hotplug](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-hotplug) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-hotplug/badge.svg) | 定义一套基础架构,实现可插拔系统 | 试用 |
| [nutz-plugins-iocloader](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-iocloader) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-iocloader/badge.svg) | 演示自定义IocLoader的用法 | 试用 |
| [nutz-plugins-jsonrpc](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-jsonrpc) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-jsonrpc/badge.svg) | 完整实现jsonrpc, 用Mapper方式 | 试用 |
| [nutz-plugins-mock](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-mock) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-mock/badge.svg) | 提供单元测试所需要的一切东西 | 开发中 |
| [nutz-plugins-multiview](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-multiview/badge.svg) | 集合N种模板引擎,可配置性强 | **生产** |
| [nutz-plugins-nop](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-nop) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-nop/badge.svg) | NUTZ OPEN PLATFORM | 测试 |
| [nutz-plugins-oauth2-server](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-oauth2-server) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-oauth2-server/badge.svg) | 使用Apache Oltu 搭建Oauth2 Server及Client开放授权 | **生产** |
| [nutz-plugins-protobuf](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-protobuf) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-protobuf/badge.svg) | 提供protobuf双向通信所需要的适配器和View | **生产** |
| [nutz-plugins-qrcode](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-qrcode) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-qrcode/badge.svg) | 生成 QRCode,基于 [zxing](http://code.google.com/p/zxing/) | **生产** |
| [nutz-plugins-secken](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-secken) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-secken/badge.svg) | 完整实现洋葱登陆协议 | **生产** |
| [nutz-plugins-sfntly](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-sfntly) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-sfntly/badge.svg) | sfntly的fork版本,修正错误并添加可编程调用 | **生产** |
| [nutz-plugins-sigar](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-sigar) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-sigar/badge.svg) | 深度集成sigar | 试用 |
| [nutz-plugins-slog](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-slog) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-slog/badge.svg) | 注解式系统日志 | 试用 |
| [nutz-plugins-spring-boot-starter](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-spring-boot-starter) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-spring-boot-starter/badge.svg) | 待编写 | **生产** |
| [nutz-plugins-sqltpl](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-sqltpl) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-sqltpl/badge.svg) | 支持多种模板引擎 | 试用 |
| [nutz-plugins-validation](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-validation) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-validation/badge.svg) | 独立,小巧且够用的校验库 | 试用 |
| [nutz-plugins-views](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-views) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-views/badge.svg) | freemarker/velocity/thymeleaf/pdf 视图插件 | **生产** |
| [nutz-plugins-webqq](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-webqq) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-webqq/badge.svg) | webqq集成 | 开发中 |
| [nutz-plugins-websocket](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-websocket) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-websocket/badge.svg) | 为websocket提供支持 | **生产** |
| [nutz-plugins-xmlentitymaker](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-xmlentitymaker) | ![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nutz/nutz-plugins-xmlentitymaker/badge.svg) | 使用xml定义实体,替换原生的注解方式 | 试用 |
