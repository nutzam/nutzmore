## NOP on Nutz 

简介(可用性:生产,维护者:王贵源)
==================================

NUTZ OPEN PLATFORM

使用nutz进行开放平台开发的签名验签机制的实现

### 实现原理

- 概述
    为防止中间人攻击,将请求参数特征按照一定算法进行签名,在服务器端对数据签名进行验证,如果数据被篡改那么签名将不能通过.同时,为了确保签名所使用的appSecret不被暴力破解,服务器端对请求到达时间进行了处理,对较长时间到达的请求予以拒绝.签名获取为了完全保留请求参数特征,设计了如果下的签名方法: 

- 签名
    + Get请求
        + queryString的MD5摘要和appSecret,时间戳,请求路径,随机字符串进行字典序排序后连接成字符串后根据签名摘要算法获取摘要作为签名
        + 如果queryString为空则处理为空字符串
    + Post请求
        + 表单方式,使用表单的urlEncodeString作为输入流获取MD5和appSecret,时间戳,请求路径,随机字符串进行字典序排序后连接成字符串后根据签名摘要算法获取摘要作为签名
        + body流,使用body流的MD5摘要和appSecret,时间戳,请求路径,随机字符串进行字典序排序后连接成字符串后根据签名摘要算法获取摘要作为签名
        + 文件上传,按照表单方式处理,其中文件值使用文件的MD5值而不是文件内容
        + 参数排序,获取urlEncodeString时涉及到参数的顺序问题,一律使用参数key的字典序进行处理
    + 其他语言实现
        按照以上规则实现签名,并将签名值appKey,时间戳,随机字符串以header方式进行传输即可
- 验签
    服务器端进行验签操作,使用和签名端一致的方式进行签名验证,同时验证时间间隔,对于较长时间到达服务器的请求将拒绝(可能存在中间人攻击的风险)
    

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
**注意:** 如果使用servlet3.0以上的容器,可以不用申明这部分,默认的调用点为/nop.endpoint

- *在classPath下添加nop.properties,名称同web.xml中的config参数值即可

```properties
	# 签名摘要方式
	digestName=MD5
	# 接口超时校验时间
	timeout=600
```
- *实现 AppsecretFetcher
实现接口org.nutz.plugins.nop.core.sign.AppsecretFetcher并声明为iocBean
- 开发
按照NUTZ手册进行接口开发即可

- tips
    - 服务端的 Appsecret应该设计成和appKey没有任何关系的随机字符串
    - Appsecret应可以通过很便捷的方式进行修改且修改可及时生效
    - appSecret 长度应该较长,建议使用16或者32位uuid,以减少被暴力破解的可能性

### 客户端实现(服务调用)

- 用于实现NOP客户端进行服务的调用

```java
package org.nutz.nop.test;

import org.junit.Before;
import org.nutz.lang.Lang;
import org.nutz.plugins.nop.client.NOPClient;

public class Base {

	protected NOPClient client;

	@Before
	public void init() {
		/**
		 * 1.调用点本机 <br/>
		 * 2.服务器端实现的appSecret仅仅是取appKey的md5,生成环境可能是从数据库获取的 <br/>
		 * 3.签名算法使用的是SHA1 <br/>
		 */
		client = NOPClient.create("test", Lang.md5("test"), "http://localhost:8080/nop-demo/nop.endpoint", "SHA1");
	}

}


@Test
public void args() {
	int i = R.random(0, 100);
	String s = R.sg(10).next() + "中文";
	Date d = Times.now();
	Response response = client.send(NOPRequest.create("/post/args", METHOD.POST, NutMap.NEW().addv("i", i).addv("s", s).addv("d", Times.format("yyyy-MM-dd HH:mm:ss", d))));
	if (response.isOK()) {
		NutMap data = Lang.map(response.getContent());
		System.err.println(data);
		assertEquals(i, data.getInt("i"));
		assertEquals(s, data.getString("s"));
		assertEquals(Times.format("yyyy-MM-dd HH:mm:ss", d), Times.format("yyyy-MM-dd HH:mm:ss", data.getTime("d")));
	}
}
```
- 同IOC容器一起工作
	+ nutz-ioc
	```javaScript	
	client:{
		type : "org.nutz.plugins.nop.client.NOPClient",
		args : ["appKey","appSecret","endpoint","digestName"],
		factory:"org.nutz.plugins.nop.client.NOPClient#create"
	}
	```
	+ spring容器 
	```xml
	<bean id="client" class="org.nutz.plugins.nop.client.NOPClient" factory-method="create">
		<constructor-arg index="0" value="appKey" />
		<constructor-arg index="1" value="appSecret" />
		<constructor-arg index="2" value="endpoint" />
		<constructor-arg index="3" value="digestName" />
	</bean>
	```
- tips
    - 千万不要泄露自己应用的appSecret
    - appSecret 不要硬编码在代码中
    - 开发环境的appSecret和生成环境的appSecret可使用环境感知环境变量等方式进行加载,以确保安全
