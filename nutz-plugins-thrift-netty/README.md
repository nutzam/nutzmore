### Thrift-Netty on Nutz 

简介(可用性:试用,维护者:Rekoe)
==================================

深度集成thrift-netty
## 集成

### 添加依赖
 
``` xml
	<dependency>
            <groupId>org.nutz</groupId>
            <artifactId>nutz-plugins-thrift-netty</artifactId>
            <version>1.r.62</version>
        </dependency>
```

```java
	@At("/echo/?")
	@Ok("raw")
	@Aop("TCOMPACTPROTOCOL")
	public String echo(String info) throws TException {
		TProtocol protocol = protocol();
		TSegmentService.Client client = new TSegmentService.Client(protocol);
		return client.getArabicWords(info);
	}
```

### conf.properties 中添加

```
thrift.host=localhost
thrift.port=17424
```

### 添加加载 

```java
@IocBy(type = ComboIocProvider.class, args = { "*org.nutz.plugins.thrift.ThriftIocLoader" })
```

#### 如果启动自己的服务 请加载

```java
ioc.get(NutThriftNettyFactory.class, "thriftFactory").serverPort(port).tProtocolFactory(new TCompactProtocol.Factory()).load("pkg.service.impl");
```

### thrift DTL文件生成插件 

```xml
	<plugin>
		<groupId>com.facebook.mojo</groupId>
		<artifactId>swift-maven-plugin</artifactId>
		<version>${swift-version}</version>
		<configuration>
			<codeFlavor>java-immutable</codeFlavor>
			<skip>false</skip>
			<idlFiles>
				<directory>${project.basedir}/src/main/thrift/</directory>
				<includes>
					<include>**/*.thrift</include>
				</includes>
				<!--<excludes> -->
				<!--<exclude>**/other.thrift</exclude> -->
				<!--</excludes> -->
			</idlFiles>
			<addCloseableInterface>true</addCloseableInterface>
			<defaultPackage>${project.groupId}.thrift.swift</defaultPackage>
			<outputFolder>${project.basedir}/src/main/java/</outputFolder>
			<addThriftExceptions>true</addThriftExceptions>
		</configuration>
	</plugin>
```

https://github.com/facebook/swift

### 执行命令

```
mvn com.facebook.mojo:swift-maven-plugin:generate
```