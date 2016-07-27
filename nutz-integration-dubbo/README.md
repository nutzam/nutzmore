Nutz集成Dubbo的插件
======================

简介(可用性:开发中)
==================================

兼容原生dubbo配置文件

* 一个预定义的IocLoader,包含Dubbo启动所需要的bean的定义
* 一个Dubbo注解扫描器

使用方法
-------------------------

* 添加dubbo依赖及本插件的依赖
* 在MainModule的IocBy中直接 *dubbo
* 在MainSetup的init方法中获取

示例IocBy配置
----------------------------------------------

```java
	@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/",
										   "*anno", "net.wendal.nutzbook",
										   "*dubbo", "dubbo-client.xml"}) // 配置文件的路径
```
								   
在MainSetup的init方法中加载(TODO)
-----------------------------------------------

```java
	ioc.get(DubboMaster.class);
	// 该操作会触发Service的初始化
```

TODO
-------------------------------------------------

* 支持dubbo:parameter标签
* 支持dubbo:method标签
* 支持dubbo:argument标签
* 支持一次性初始化所有Bean

客户端调用实例
--------------------------------------------------

新建一个接口 

```java
package net.wendal.nutzbook.service;

public interface DubboWayService {

    String redisSet(String key, String value);
    
    String redisGet(String key);

    String hi(String name);

}
```

新建一个配置文件 dubbo-client.xml, 使用dubbo原生配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        http://www.springframework.org/schema/beans/spring-beans.xsd        http://code.alibabatech.com/schema/dubbo        http://code.alibabatech.com/schema/dubbo/dubbo.xsd">
 
    <dubbo:application name="nutzcn-client"  />
 
    <dubbo:registry address="multicast://224.5.6.7:1234" />
 
    <dubbo:protocol name="dubbo" port="20880" />
 
    <dubbo:reference id="dubboWayService" interface="net.wendal.nutzbook.service.DubboWayService" url="dubbo://nutz.cn:20880/net.wendal.nutzbook.service.DubboWayService"/>
 
</beans>
```

添加测试类

```java
package org.nutz.integration.dubbo.service;

import static org.junit.Assert.*;

import org.junit.*;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;

import net.wendal.nutzbook.service.DubboWayService;

public class DubboClientTest {

    Ioc ioc;
    
    @Before
    public void before() throws ClassNotFoundException {
        // 载入配置
        ComboIocLoader loader = new ComboIocLoader("*dubbo", "dubbo-client.xml");
        ioc = new NutIoc(loader);
    }
    
    @After
    public void after() {
        if (ioc != null)
            ioc.depose();
    }

    @Test
    public void test_simple_hi() throws Exception {
        // 获取引用
        DubboWayService way = ioc.get(DubboWayService.class, "dubboWayService");
        
        // 执行调用
        String resp = way.hi("wendal");
        assertEquals(resp, "hi,wendal");
    }
}
```

