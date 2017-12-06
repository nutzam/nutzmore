nutz-integration-swagger
==================================

简介(可用性:试用,维护者:wendal)
==================================

轻度集成swagger

功能介绍
==================================

演示如何集成Swagger.

服务端用法
==================================

### maven依赖

只需要引入swagger

```xml
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

    @Ok("void")
    @At
    public void swagger(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if ("true".equals(request.getParamter("force")))
            init(); //强制刷新
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
        Info info = new Info();
        info.title("ABC....");
        swagger.info(info);
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

### 添加swagger注解

```java
@Api(value = "demo")
@IocBean
@At("/demo")
public class SwaggerDemoModule {

    @GET
    @ApiOperation(value = "心跳接口", notes = "发我一个ping,回你一个pong", httpMethod="GET")
    @At
    @Ok("json:full")
    public Object ping() {
        return new NutMap("ok", true).setv("data", "pong");
    }

    @POST
    @ApiOperation(value = "回显接口", notes = "发我一个字符串,原样回复一个字符串", httpMethod="POST")
    @ApiImplicitParams({@ApiImplicitParam(name = "text", paramType="form", value = "想发啥就发啥", dataType="string", required = true)})
    @At
    @Ok("raw")
    public String echo(@Param("text") String text) {
        return text;
    }
}
```

访问 http://localhost:8080/nutzcn/swagger/ 即可看到swagger的界面