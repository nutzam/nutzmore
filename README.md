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
			<version>1.r.57</version>
		</dependency>
```

# 快照版地址

https://oss.sonatype.org/content/repositories/snapshots/org/nutz/

```xml
	<repositories>
		<repository>
			<id>ossrh</id>
			<url>https://oss.sonatype.org/content/repositories/snapshots</url>
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

| 插件名称 | 简介 | 可靠性 |
| ------| ------ | ------ |
|nutz-integration-autoloadcache|深度集成AutoLoadCache|**生产**|
|nutz-integration-dubbo|兼容原生dubbo配置文件|试用|
|nutz-integration-dwr|轻度集成dwr|试用|
|nutz-integration-hessian|提供hessian的Mvc适配器|**生产**|
|nutz-integration-jcache|集成Jcache的方法级注释|试用|
|nutz-integration-jsch|简单演示jsch的端口映射|试用|
|nutz-integration-jsf|提供JSF集成所需要的EL解析器|试用|
|nutz-integration-jsr303|深度集成jsr303的校验机制|试用|
|nutz-integration-quartz|集成Quartz(计划任务/定时任务)的不二选择|**生产**|
|nutz-integration-shiro|集成Shiro的登陆,鉴权,和Session机制|**生产**|
|nutz-integration-spring|Spring集成NutDao时所需要的事务代理及NutIoc集成Spring的代理类|**生产**|
|nutz-integration-struts2|替换struts2的Ioc容器为NutIoc|试用|
|nutz-integration-zbus|深度集成zbus,提供mq,rpc支持|试用|
|nutz-plugins-daocache|为NutDao提供缓存支持,SQL级别的缓存|**生产**|
|nutz-plugins-iocloader|演示自定义IocLoader的用法|试用|
|nutz-plugins-jedis|轻度集成jedis|开发中|
|nutz-plugins-jsonrpc|完整实现jsonrpc, 用Mapper方式|试用|
|nutz-plugins-multiview|集合N种模板引擎,可配置性强|**生产**|
|nutz-plugins-oauth2-server|待编写|开发中|
|nutz-plugins-protobuf|提供protobuf双向通信所需要的适配器和View|**生产**|
|nutz-plugins-secken|完整实现洋葱登陆协议|**生产**|
|nutz-plugins-sfntly|sfntly的fork版本,修正错误并添加可编程调用|**生产**|
|nutz-plugins-sigar|深度集成sigar|开发中|
|nutz-plugins-sqltpl|支持多种模板引擎|试用|
|nutz-plugins-validation|独立,小巧且够用的校验库|试用|
|nutz-plugins-views|包含freemarker/velocity/thymeleaf 视图插件|**生产**|
|nutz-plugins-webqq|webqq集成|开发中|
|nutz-plugins-xmlentitymaker|使用xml定义实体,替换原生的注解方式|试用|
