nutz-plugins-apidoc
==================================

简介(可用性:生产,维护者:wendal)
==================================

API文档生成及调试

## 用法

在MainModule添加如下配置

```java
@UrlMappingBy(ApidocUrlMapping.class)
```

重启后,访问下面的地址

http://127.0.0.1:8080/项目名称/_/

演示地址: https://nutz.cn/_/

## 注解

配合@Api和@ApiParam等注解,可以生成更友好的文档.

TODO 待完成.


## 调试按钮

仅支持Chrome内核的浏览器,且安装DHC Rest Client插件

https://chrome.google.com/webstore/detail/dhc-rest-client/aejoelaoggembcahagimdiliamlcdmfm/support?utm_source=chrome-app-launcher-info-dialog