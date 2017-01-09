nutz-plugins-slog
==================================

简介(可用性:试用)
==================================

注解式系统日志

用法
==================================

MainModule的IocBy启用该插件

```java
@IocBy(args={
	"*js", "ioc/",
	"*anno", "net.wendal.nutzbook",
	"*async", // 建议一起启用,否则@SLog(async=true)不会生效
	"*slog" // 启用即可
})
```

需要日志记录的方法

```java
@Slog(tag="新增yongh", after="用户id=${re.id}")
public User add(User user) {
    return dao.insert(user);
}
```

@Slog注解详解
======================================

* tag 标识符
* before/after/error 分别代表日志记录的时机. 方法执行前,执行后,抛出异常时
* async 是否异步记录,默认为真,依赖于"*async"是否启用

可用变量

* args 方法参数
* re 方法返回值,仅 after时可用
* e 异常对象,仅error时可用
* req 请求对象,仅mvc请求作用域内可用
* resp 响应对象,仅mvc请求作用域内可用

扩展与自定义
=======================================

### 自定义获取用户id的方法


```java
    SlogService.GET_USER_ID = new Callable<Object>() {
        public String call() throws Exception {
            return 你的用户标示; // 默认走shiro的登录信息.
        };
    };
```

### 扩展SlogService

通过继承SlogService,并定于为同名bean(slogService),可覆盖默认实现.

```java
@IocBean(name="slogService", fields={"dao"})
public class MySlogService extends SlogService {

}
```

