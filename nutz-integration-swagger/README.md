nutz-integration-swagger
==================================

简介(可用性:试用,维护者:wendal)
==================================

轻度集成swagger

功能介绍
==================================

1. Swagger Extension for Nutz
2. SwaggerAbstractModule

完成情况
==================================

- [ ] Swagger Extension for Nutz
- [ ] SwaggerAbstractModule


服务端用法
==================================

### maven依赖

TODO

```xml
		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>nutz-integration-swagger</artifactId>
			<version>1.r.63-SNAPSHOT</version>
		</dependency>
		<dependency>
			<groupId>io.swagger</groupId>
			<artifactId>swagger-core</artifactId>
			<version>1.5.16</version>
		</dependency>
		<dependency>
			<groupId>io.swagger</groupId>
			<artifactId>swagger-servlet</artifactId>
			<version>1.5.16</version>
		</dependency>
```

### 添加SwaggerModule

```java
@IocBean(create = "init")
@At("/swagger")
public class SwaggerModule {

    private static final Log log = Logs.get();

    protected Swagger swagger;

    @At
    public void swagger(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final String pathInfo = request.getRequestURI();
        if (pathInfo.endsWith("/swagger.json")) {
            response.setContentType("application/json");
            response.getWriter().println(Json.mapper().writeValueAsString(swagger));
        } else if (pathInfo.endsWith("/swagger.yaml")) {
            response.setContentType("application/yaml");
            response.getWriter().println(Yaml.mapper().writeValueAsString(swagger));
        } else {
            response.setStatus(404);
        }
    }

    public void init() {
        log.info("init swagger ...");
        swagger = new Swagger();
        HashSet<Class<?>> classes = new HashSet<>();
        // 把下来的package路径改成你自己的package路径
        for (Class<?> klass : Scans.me().scanPackage("net.wendal.nutzbook.swagger")) {
            classes.add(klass);
        }
        Reader.read(swagger, classes);
    }
}
```

### 添加swagger-ui

在src/main/webapp(maven项目)或者WebContent(Eclipse普通JavaEE项目)下,建一个swagger目录

从 https://github.com/swagger-api/swagger-ui/releases 下载最新,取里面dst目录

将dst目录里面的文件统统放入swagger目录,效果如下

```
- src
	- main
		- webapp
			- swagger
				- index.html
				- swagger-ui.js
				- ...
```

然后修改index.html里面的

```
url: "http://petstore.swagger.io/v2/swagger.json",
// 改成
url: "./swagger.json",

```