Nutz 热插拔
==================================

简介(可用性:开发中)
==================================

定义一套基础架构,实现可插拔系统

背景
==================================

nutzcn,nutzwk,nutz-onekey,rkcms, 都定义了一套自己的模式, 即package组织方式,各种类的命名,等等

那是不是能够定义一套比nutz框架本身严格一点点的规范,减少重复开发的呢? 例如nutzcn需要cms,可否与rkcms共享一套代码呢?

还有一个动机是这个帖子[求开发组，开发一个nutz的osgi插件](https://nutz.cn/yvr/t/23ogp0e7lqi4vrpnlsgqqt2bhd)

诚然,实现osgi的意义,我个人觉得真的不大,太复杂,约束得太死.

所以,我尝试定义了nutz-plugins-hotplug,它比nutz框架的约束高不少,但比osgi低很多.

另外, 很重要的一点,就需要做的是, 开发时可一起调试(放在一个项目里或maven多模块),部署时可以按需加载(裸核心+插件jar)

要实现的功能
====================================

* [x]支持@IocBean, 实现动态添加Ioc对象
* [x]支持@At, 实现动态添加入口方法
* []支持@SetupBy. 当前是在hotplug.XXX.json里面声明setup
* []支持@Localization, 国际化的字符串
* []插件间的类引用,已完成,未详细测试
* []动态后台菜单
* []Junit支持

裸核心
====================================

是什么是裸核心, 它只提供了基础服务,与具体业务无关的服务, 例如权限(shiro),模板引擎(beetl/freemarker等),SQL/NoSQL支持,计划任务(Quartz)

它是一个maven工程,它应该足够强壮. 它有一个参考实现, nutzcn 3.x的源码

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

### 为了实现开发器的联调, MainModule必须标注以下注解

```java
@LoadingBy(HotPlug.class)
```

### 且MainSetup类必须带下面的代码

```java
public void init(NutConfig nc) {
	// 初始化其他插件
	nc.getIoc().get(HotPlug.class).setupInit();
}

public void destroy(NutConfig nc) {
	// 销毁插件
	nc.getIoc().get(HotPlug.class).setupDestroy();
	
	// 其他代码
}
```

插件的源码项目结构
==============================

yvr插件的项目结构如下, 可以与裸核心放在一个项目中. 插件的名称是yvr,所以下面的名称是有要求的

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
							- job
								- YvrCheckJob.jav
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

相关文件的含义

* YvrMainModule 一个空类,属于占位符. 必选,但并不需要标注注解,同时也不需要标注.
* hotplug.yvr.json 插件描述文件,必选, 后面会有详细描述.
* YvrMainSetup 插件的初始化类. 必选,在插件初始化和销毁之前会调用,跟MainSetup一样
* bean/service/module Pojo类/Service类/Module类所在package,这三个package的命名不严格要求
* job 定时任务类的package,暂时约定
* assets/yvr 插件所需要的静态资源文件目录,可选
* templates/yvr 模板文件的目录,可选

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
    protected String name;
    
    /**
     * 版本号,当前仅供描述用,可选
     */
    protected String version;
    
    /**
     * 插件必须有自己的顶层package,必须有
     */
    protected String base;
    /**
     * 插件自身的MainModule,属性可以,默认是插件名字+MainModule
     */
    protected String main;
    /**
     * 插件的Setup实现类,属性可以,默认是插件名字+MainSetup
     */
    protected String setup;
```
