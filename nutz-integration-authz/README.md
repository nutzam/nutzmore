Nutz集成jCasbin的插件
======================

简介(可用性:生产,维护者:[hsluoyz](https://github.com/hsluoyz))
==================================

集成[jCasbin](https://github.com/casbin/jcasbin)的访问控制、权限管理功能

本插件的主要部件
-------------------------

* JCasbinAuthzFilter HTTP请求过滤器
* HttpBasicAuthnFilter HTTP基础身份认证（HTTP basic authentication），用来测试本插件功能，可以替换成其他实现

使用方法
-------------------------

* 添加本插件及jCasbin的依赖, 支持1.2+版本,建议用最新版
* 添加jCasbin的模型文件: auth_model.conf 和策略文件: auth_policy.csv,详情请参考：[如何读写jCasbin模型](https://github.com/casbin/casbin/wiki/Model-persistence)以及[如何读写jCasbin策略](https://github.com/casbin/casbin/wiki/Policy-persistence)；
* 修改JCasbinAuthzFilter类的getUser函数，在内部获取身份认证后的用户名, 从而让本插件知道当前访问的用户
* 在web.xml中添加ShiroFilter

添加本插件及依赖
-----------------------------

```xml
<dependency>
	<groupId>org.nutz</groupId>
	<artifactId>nutz-integration-authz</artifactId>
	<version>1.r.60</version>
</dependency>
```

教程
-----------------------------

- [比Shiro更简单的Nutz权限管理：与jCasbin权限管理框架进行整合](https://nutz.cn/yvr/t/7v1m8jh2qejo7qu5460m2qgmul)

帮助
-----------------------------

- [jCasbin](https://github.com/casbin/jcasbin/issues)
