### NOP on Nutz 

简介(可用性:测试)
==================================

NUTZ OPEN PLATFORM

### 服务端集成(Servlet)

用于通过NOP提供服务

-  添加依赖

 ```xml
 	<dependency>
  		<groupId>org.nutz</groupId>
  		<artifactId>nutz-plugins-nop</artifactId>
  		<version><!-- nop-version --></version>
  	</dependency>
 ```
-  在web.xml中添加

```xml
	<servlet>
		<servlet-name>nop</servlet-name>
		<servlet-class>org.nutz.plugins.nop.server.NOPServlet</servlet-class>
		<init-param>
			<!-- 如果配置路径为nop.properties可以不设置 -->
			<param-name>config</param-name>
			<param-value>nop.properties</param-value>
		</init-param>
	</servlet>
	<servlet-mapping>
		<servlet-name>nop</servlet-name>
		<url-pattern>/endpoint</url-pattern>
	</servlet-mapping>
```

- 在classPath下添加nop.properties,名称同web.xml中的config参数值即可

```properties
	# 签名摘要方式
	digestName=MD5
	# org.nutz.plugins.nop.core.sign.AppsecretFetcher接口的实现类在ioc中的beanName
	fetcherName=default
	# 接口超时校验时间
	timeout=600
```
- 实现

- 声明AppsecretFetcher
实现接口org.nutz.plugins.nop.core.sign.AppsecretFetcher并声明为iocBean beanName和properties配置中一致即可

```java
package club.zhcs.thunder.module;

import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.plugins.nop.NOPConfig;
import org.nutz.plugins.nop.core.NOPData;
import org.nutz.plugins.nop.server.NOPSignFilter;

import club.zhcs.titans.nutz.module.base.AbstractBaseModule;

@At("test")
@Filters(@By(type = NOPSignFilter.class))
public class NOPModule extends AbstractBaseModule {

	@At
	public NOPData hello() {
		return NOPData.success().addData("msg", "hello nop");
	}

	@At
	public NOPData calc(@Attr(NOPConfig.parasKey) NutMap data) {//请求数据存储在请求的 NOPConfig.parasKey 属性中

		return NOPData.success().addData("r", data);
	}

	@At
	public NOPData file(@Attr(NOPConfig.parasKey) NutMap data) {

		return NOPData.success().addData("r", data);
	}

}
```

### 客户端实现(服务调用)

用于实现NOP客户端进行服务的调用

```java
package org.nutz.plugins.nop;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.nutz.http.Header;
import org.nutz.http.Request.METHOD;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.nop.client.NOPClient;
import org.nutz.plugins.nop.core.NOPRequest;
import org.nutz.plugins.nop.core.serialize.UploadFile;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file ClientTest.java
 *
 * @description
 *
 * @time 2016年8月31日 下午8:46:03
 *
 */
public class ClientTest {
	NOPClient client;

	@Before
	public void init() {
		client = NOPClient.create("abcd", Lang.md5("abcd"), "localhost:8080/nop-demo/endpoint", "MD5");//初始化客户端,生成环境一般处理为单例
	}

	@Test
	public void post() {
		System.err.println(Json.toJson(client.send(NOPRequest.create("test/hello", METHOD.POST, NutMap.NEW(), Header.create())).getData()));//无参请求
	}

	@Test
	public void calc() {
		System.err
				.println(Json.toJson(client.send(NOPRequest.create("test/calc", METHOD.POST, NutMap.NEW().addv("a", 5).addv("b", new int[] { 2, 3 }), Header.create())).getData()));//传递参数
	}

	@Test
	public void file() {
		System.err.println(Json.toJson(client.send(
				NOPRequest.create("test/file", METHOD.POST, NutMap.NEW().addv("a", 5).addv("b", new UploadFile(new File("D:\\a.sql"))), Header.create())).getData()));//上传文件
	}
}
```