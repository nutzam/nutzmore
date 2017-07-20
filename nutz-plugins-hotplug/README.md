Nutz 热插拔与模块化
==================================

简介(可用性:生产,维护者:wendal)
==================================

定义一套基础架构,实现可插拔系统

背景
==================================

nutzcn,nutzwk,nutz-onekey,rkcms, 都定义了一套自己的模式, 即package组织方式,各种类的命名,等等

那是不是能够定义一套比nutz框架本身严格一点点的规范,减少重复开发的呢? 例如nutzcn需要cms,可否与rkcms共享一套代码呢?

还有一个动机是这个帖子[求开发组，开发一个nutz的osgi插件](https://nutz.cn/yvr/t/23ogp0e7lqi4vrpnlsgqqt2bhd)

实现osgi的意义,我个人觉得真的不大,太复杂,约束得太死.

所以,我尝试定义了nutz-plugins-hotplug,它比nutz框架的约束高不少,但比osgi低很多.

另外, 很重要的一点,开发时可一起调试(maven多模块),部署时可以按需加载(裸核心+插件jar), 要做到无缝


要实现的功能
====================================

- [x] 支持子模块的@At,实现动态添加入口方法
- [x] 支持子模块的@IocBean, 实现动态添加Ioc对象
- [x] 支持子模块的@SetupBy
- [x] 支持子模块的IocBy
- [x] 支持插件间的类引用
- [x] 支持Beetl模块的动态查找
- [x] 支持子模块的网页静态文件(js/css/png/jpg等)的动态查找
- [ ] 支持子模块的@Localization, 国际化的字符串
- [ ] Junit支持

什么是插件,什么是模块
===================================

* 模块,指的是maven的一个module
* 插件,指符合hotplug插件规范的模块

一个完整的hotplug项目包含什么
==================================

* 一个parent模块,作为其他模块的根
* core插件模块和其他插件模块
* 一个webapp模块,引用core插件模块,组成最基础的运行内核
* 其他辅助模块

扩展阅读
===================================

* [Hotplug插件规范](hotplug_module.md)
* [webapp模块的必备设置](webapp_module.md)
* [Hotplug的官方参考实现](https://github.com/wendal/nutz-book-project/tree/v3.x)
* [Hotplug实现思路](how_it_work.md)