Nutz 热插拔与模块化
==================================

简介(可用性:试用)
==================================

定义一套基础架构,实现可插拔系统

背景
==================================

nutzcn,nutzwk,nutz-onekey,rkcms, 都定义了一套自己的模式, 即package组织方式,各种类的命名,等等

那是不是能够定义一套比nutz框架本身严格一点点的规范,减少重复开发的呢? 例如nutzcn需要cms,可否与rkcms共享一套代码呢?

还有一个动机是这个帖子[求开发组，开发一个nutz的osgi插件](https://nutz.cn/yvr/t/23ogp0e7lqi4vrpnlsgqqt2bhd)

实现osgi的意义,我个人觉得真的不大,太复杂,约束得太死.

所以,我尝试定义了nutz-plugins-hotplug,它比nutz框架的约束高不少,但比osgi低很多.

另外, 很重要的一点,就需要做的是, 开发时可一起调试(放在一个项目里或maven多模块),部署时可以按需加载(裸核心+插件jar)

名称约定
==================================

MainModule 指项目的MainModule类,通常放在webapp模块内,是写在web.xml中的类名
MainSetup 指项目的Setup实现类,需要调用hotplug的初始化代码.
XXXMainModule 子模块的MainModule类
XXXMainSetup 子模块的Setup类
assets 静态资源文件存放目录,通常是js/css/png/jpg等供浏览器访问的文件
templates 模板文件目录
模块/插件 两个比较混淆的概念,在本文章中基本上是一个东西

要实现的功能
====================================

* [x]支持@IocBean, 实现动态添加Ioc对象
* [x]支持@At, 实现动态添加入口方法
* [X]支持@SetupBy
* [X]支持IocBy
* [X]插件间的类引用
* []支持@Localization, 国际化的字符串
* []Junit支持

### 整个项目会分成几个部分

* 核心模块core 各种业务无关的代码,公用代码,基础代码
* web模块webapp 整合各种模块,总得有个东西启动http服务哦
* 插件模块 各种业务模块,依赖core. 开发时被web模块引用, 部署时可能以插件,也可能直接集成在war中

核心模块
====================================

什么是核心模块, 它只提供了基础服务,与具体业务无关的服务, 例如权限(shiro),模板引擎(beetl/freemarker等),SQL/NoSQL支持,计划任务(Quartz)

它有一个参考实现, 它在[nutzcn(nutz-book-project) 3.x的源码](https://github.com/wendal/nutz-book-project/tree/v3.x)

```
src
	- main
		- java
			- net
				- wendal
					- nutzbook
						- MainModule
						- MainSetup
						- common
							- shiro
							- beetl
							- quartz
						- core
							- service
							- module
							- bean
							- CoreMainModule.java
							- CoreMainSetup.java
		- resources
			- ioc
				- dao.js
				- mail.js
				- jpush.js
				- xmpush.js
			- msg
				- zh_CN
					- core.properties
			- custom
				- dao.properties
				- redis.properties
				- mail.properties
				- jpush.properties
				- xmpush.properties
		- webapp
			- asserts
				- common
					- js
					- css
					- font
			- WEB-INF
				- web.xml
```

插件的源码项目结构
==============================

yvr插件的项目结构如下, 推荐使用maven子模块的方式, 但也支持与核心模块放在一个项目中. 

* 它必须有自己MainModule, 部分注解是支持的,命名为XXXMainModule
* 通常,它有自己的MainSetup类, 命名为XXXMainSetup
* 通常,它有自己的bean/module/service包
* 它必须有一个hotplug.xxx.json文件,用于描述这个差距,我在考虑是否应该做成注解
* 它可以有一个assets目录,用于存放js/css等文件
* 它可以有一个templates目录,用于存放页面模板
* 它通常只依赖核心模块
* webapp模块也是一个模块,只是比较特别的模块,不需要遵循插件的规则, 它需要把各种插件启动起来

```
src
	- main
		- java
			- net
				- wendal
					- nutzbook
						- yvr
							- YvrMainModule
							- YvrMainSetup
							- bean
								- Topic.java
								- TopicReply.java
							- service
								- YvrService.java
							- module
								- YvrModule.java
								- YvrApiModule.java
		- resources
			- hotplug
				- hotplug.yvr.json
			- assets
				- yvr
					- js
					- css
					- font
			- templates
				- yvr
					- layout.html
					- index.html
					- user
						- index.html
```

插件jar的结构
====================================================

插件jar,即插件编译后,打包成插件时所需要遵循的目录结构. 以yvr插件为例

```
- hotplug
	- hotplug.yvr.json
- net
	- wendal
		- nutzbook
			- yvr
				- YvrMainModule
				- YvrMainSetup
				- bean
					- Topic.class
- templates
	- yvr
		- layout.html
		- index.html
		- user
			- index.html
- assets
	- yvr
		- js
			- yvr_index.js
		- css
			- yvr_index.css
```

hotplug.XXX.json文件的格式要求
===================================================

如它名字所示,它是一个json文件,是HotPlugConfig序列化后的结果

```java
    /**
     * 插件的唯一命名,必须有
     */
    name : "yvr"
    
    /**
     * 版本号,必须有
     */
    version : "3.0.1",
    
    /**
     * 插件必须有自己的顶层package,必须有,但并不强制要求所有类都在该package下
     */
    base : "net.wendal.nutzbook.yvr"
    /**
     * 插件自身的MainModule,属性可以,默认是插件名字+MainModule
     */
    main : "net.wendal.nutzbook.yvr.YvrMainModule";
```

web模块的核心配置
=============================================

通常指webapp模块,用于启动web服务的项目,它不需要遵循插件规范,因为是它来启动整个插件体系


### MainModule标注以下注解

```java
@LoadingBy(HotPlug.class)   // 改变加载行为
@Modules(scanPackage=false) // 禁用自动扫描
@SetupBy(MainSetup.class) // 加载下一个小节的MainSetup类
```

### MainSetup类带下面的代码

```java
public void init(NutConfig nc) {
	// 初始化插件系统
	nc.getIoc().get(Hotplug.class).setupInit();
}

public void destroy(NutConfig nc) {
	// 销毁插件系统
	nc.getIoc().get(Hotplug.class).setupDestroy();
	
	// 其他代码
}
```