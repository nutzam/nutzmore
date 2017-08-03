nutz-integration-cxf
==================================

简介(可用性:开发中,维护者:wendal)
==================================

轻度集成cxf(WebService)

功能介绍
==================================

1. WebService入口代理
2. WebService客户端代理

完成情况
==================================

- [X] 入口代理
- [ ] 客户端代理


服务端用法
==================================

### maven依赖

```xml
		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>nutz-integration-cxf</artifactId>
			<version>1.r.63-SNAPSHOT</version>
		</dependency>
		<dependency>
			<groupId>org.apache.cxf</groupId>
			<artifactId>cxf-core</artifactId>
			<version>3.1.12</version>
		</dependency>
		<dependency>
			<groupId>org.apache.cxf</groupId>
			<artifactId>cxf-rt-transports-http</artifactId>
			<version>3.1.12</version>
		</dependency>
		<dependency>
			<groupId>org.apache.cxf</groupId>
			<artifactId>cxf-rt-frontend-simple</artifactId>
			<version>3.1.12</version>
		</dependency>
		<dependency>
			<groupId>org.apache.cxf</groupId>
			<artifactId>cxf-rt-frontend-jaxws</artifactId>
			<version>3.1.12</version>
		</dependency>
		<dependency>
			<groupId>junit</groupId>
			<artifactId>junit</artifactId>
			<version>4.12</version>
			<scope>test</scope>
		</dependency>
```

### 首先, 选定一个路径前缀

例如 /cxf,作为webservice路径前缀

### 然后自定义一个Module类,集成本插件的AbstractCxfModule

例如MyCxfModule, 填入以下内容

```java
@IocBean(create = "_init", depose = "depose")
@At("/cxf")
public class MyCxfModule extends AbstractCxfModule {

    public MyCxfModule() {
       // 与类上的@At对应
       pathROOT = "/cxf/" ;
       // 设置webservice实现类的package
       pkg = "net.wendal.nutzbook.cxfdemo.webservice";
    }

    //定义入口方法
    @At("/*")
    @Ok("void")
    @Fail("void")
    public void service() throws Exception {
        super.service();
    }
}
```

### 编写服务接口及实现类

均放在MyCxfModule的pkg声明的package下

```java
// 服务接口类仅需要声明一个空的@WebService,不可以加serviceName,endpointInterface
@WebService
public interface EchoService {

    // 测试一下无参,有返回值的
    String ping();

    // 测试一下有参数,有返回值
    String echo(@WebParam(name = "str") String str);
}

// 服务实现类, endpointInterface写服务接口的名字,serviceName可以自定义,建议就用接口的名字
@WebService(endpointInterface = "net.wendal.nutzbook.cxfdemo.webservice.EchoService",
        serviceName = "EchoService")
@IocBean(name = "echoService")
public class EchoServiceImpl implements EchoService {

    public String ping() {
        return "pong"; // ping --> pong 心跳回路
    }

    public String echo(String str) {
        return str; // echo就是回显
    }
}
```

### 手动检查效果

正常启动后,假设content path是/nutzcn, 访问地址获取WSDL定义文件

```
// 其中cxf是@At的值,EchoService是serviceName类
http://127.0.0.1:8080/nutzcn/cxf/EchoService?wsdl
```

如无意外,会显示一屏幕的xml数据,检索一下,应该有echo/ping等字眼.

### 使用客户端测试访问

在同一个项目中新建一个测试类

```java
public class CxfClientTest extends Assert {

    @Test
    public void test_local() throws MalformedURLException {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(EchoService.class);
        factory.setAddress("http://localhost:8080/nutzcn/cxf/EchoService");
        EchoService client = (EchoService) factory.create();
         
        String reply = client.echo("hi, cxf");
        System.out.println("Server said: " + reply);
        System.out.println("Server said: " + client.ping());
        assertEquals("hi, cxf", reply);
    }
}
```

### 可能遇到的问题

* 访问?wsdl报404错误,检查pkg是否正确,看看有没有扫描到EchoServiceImpl类
* 客户端报404错误,检查factory.setAddress的地址是否正确, 添加?wsdl应该出xml数据
