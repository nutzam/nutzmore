# Hotplug插件规范

插件的源码项目结构
==============================

使用maven子模块的方式, 并独占一个package. 

先来点感性认识

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

插件项目必须有什么,可以有什么
==================================

* 必须: 插件自己MainModule, 部分注解是支持的,命名为XXXMainModule
* 必须: 一个hotplug.xxx.json文件,用于描述这个插件的信息,具体定义看下一个小节
* 推荐: Setup实现类, 命名为XXXMainSetup,通过XXXMainModule的@SetupBy注解引用
* 可选: bean/module/service包
* 可选: assets目录,用于存放js/css等文件
* 可选: templates目录,用于存放页面模板
* 推荐: 若需要依赖其他插件,务必把scope设置为provied

hotplug.XXX.json文件的格式要求
===================================================

一个json文件, 用于描述插件本身的信息

```js
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
    base : "net.wendal.nutzbook.yvr",
    /**
     * 插件自身的MainModule,属性可以,默认是插件名字+MainModule
     */
    main : "net.wendal.nutzbook.yvr.YvrMainModule";
```

插件jar的格式是怎样的
==================================

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

如何打包
==================================

通过mvn的assembly命令可以打包一个插件

```
mvn clean package assembly:single
```