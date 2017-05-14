Nutz集成Dubbo的插件
======================

简介(可用性:试用)
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
								   
在MainSetup的init方法中加载
-----------------------------------------------

```java
	ioc.get(DubboManager.class);
	// 该操作会触发Service的初始化
```

TODO
-------------------------------------------------

* 完整支持@Reference

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

    @Test
    public void test_simple_hi() throws Exception {
        // 获取引用
        DubboWayService way = ioc.get(DubboWayService.class, "dubboWayService");
        
        // 执行调用
        String resp = way.hi("wendal");
        assertEquals(resp, "hi,wendal");
    }
    
    // 模拟ioc环境. 真实环境下通过@IocBy即可,千万别自行new NutIoc
    
    Ioc ioc;
    
    @Before
    public void before() throws ClassNotFoundException {
        // 载入配置, 真实环境下会直接注入需要的对象
        ComboIocLoader loader = new ComboIocLoader("*dubbo", "dubbo-client.xml");
        ioc = new NutIoc(loader);
    }
    
    @After
    public void after() {
        if (ioc != null)
            ioc.depose();
    }
}
```

服务端调用实例
--------------------------------------------------

接口及其实现类 

```java
package net.wendal.nutzbook.service;

public interface DubboWayService {

    String redisSet(String key, String value);
    
    String redisGet(String key);

    String hi(String name);

}

@IocBean(name="dubboWayService")
public class DubboWayServiceImpl {

	@Inject
	JedisAgent jedisAgent;
    pubic String redisSet(String key, String value) {
        try (Jedis jedis = jedisAgent.getResource()){
        	return jedis.set(key, value);
        }
    };
    
    public String redisGet(String key) {
    	try (Jedis jedis = jedisAgent.getResource()){
        	return jedis.get(key);
        }
    };

    public String hi(String name) {
    	return "hi," + name;
    };

}
```

新建一个配置文件 dubbo-client.xml, 使用dubbo原生配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dubbo="http://code.alibabatech.com/schema/dubbo"
    xsi:schemaLocation="http://www.springframework.org/schema/beans        http://www.springframework.org/schema/beans/spring-beans.xsd        http://code.alibabatech.com/schema/dubbo        http://code.alibabatech.com/schema/dubbo/dubbo.xsd">
 
    <dubbo:application name="nutzcn-server"  />
 
    <dubbo:registry address="multicast://224.5.6.7:1234" />
 
    <dubbo:protocol name="dubbo" port="20880" />
 
    <!-- ref对应bean的name属性 -->
    <dubbo:service id="dubboWayService" interface="net.wendal.nutzbook.service.DubboWayService" 
    ref="dubboWayService"/>
 
</beans>
```