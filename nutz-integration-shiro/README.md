Nutz集成Shiro的插件
======================

简介(可用性:生产,维护者:wendal)
==================================

集成Shiro的登陆,鉴权,和Session机制

本插件的主要部件
-------------------------

* SimpleAuthenticationFilter 穿透登陆请求到Nutz.Mvc的入口方法
* SimpleShiroToken 不带校验信息的token实现
* ShiroSessionProvider 在Nutz.MVC作用域内,使用Shiro替换容器原生的Session机制
* ShiroProxy 用于模板引擎中方便调用shiro
* LCache 多层次缓存实现,依赖nutz-plugins-cache和nutz-integration-jedis

**原CaptchaFormAuthenticationFilter已经废弃** 原因是出错了都不知道哪里错,而且不好定制.


具体实例,请参考[NutzCN论坛的源码](https://github.com/wendal/nutz-book-project)

使用方法
-------------------------

* 添加本插件及shiro的依赖, 支持1.2+版本,建议用最新版
* 继承AbstractRealm,实现一个Realm. 特别注意在构造方法内注册关联的Token类!!
* 添加 shiro.ini
* 在web.xml中添加ShiroFilter
* 添加入口方法完成登陆
* 可选: 注册ShiroSessionProvider
* 可选: 登记UU32SessionIdGenerator
* 可选: Session缓存与持久化,已迁移到nutz-plugins-cache和nutz-integration-jedis

添加本插件及依赖
-----------------------------

```xml
		<dependency>
			<groupId>org.nutz</groupId>
			<artifactId>nutz-integration-shiro</artifactId>
			<version>1.r.60</version>
		</dependency>
		<dependency>
			<groupId>org.apache.shiro</groupId>
			<artifactId>shiro-all</artifactId>
			<version>1.3.0</version>
			<exclusions>
				<exclusion>
					<artifactId>ehcache-core</artifactId>
					<groupId>net.sf.ehcache</groupId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>net.sf.ehcache</groupId>
			<artifactId>ehcache</artifactId>
			<version>2.10.1</version>
		</dependency>
```

继承AbstractRealm,实现一个Realm
--------------------------------------

这部分是跟具体项目的Pojo类紧密结合的,所以没有给出默认实现. 严重建议继承AbstractSimpleAuthorizingRealm

请参考[NutzCN论坛的源码](https://github.com/wendal/nutz-book-project)中的net.wendal.nutzbook.shiro.realm.SimpleAuthorizingRealm类

**请特别注意构造方法中注册的Token类** 在入口方法中执行SecurityUtils.getSubject().login时所使用的Token类型必须匹配!!

最简单的shiro.ini
--------------------------

其中的net.wendal.nutzbook.shiro.realm.NutDaoRealm是nutzbook中的NutDaoRealm实现.

```ini
	[main]
	nutzdao_realm = net.wendal.nutzbook.shiro.realm.SimpleAuthorizingRealm
	authc = org.nutz.integration.shiro.SimpleAuthenticationFilter
	authc.loginUrl  = /user/login

	[urls]
	/user/logout = logout
```
	
web.xml中添加ShiroFilter配置
----------------------------

必须添加在NutFilter之前,让其先于NutFilter进行初始化

```xml
	<listener>
		<listener-class>org.apache.shiro.web.env.EnvironmentLoaderListener</listener-class>
	</listener>
	<filter>
		<filter-name>ShiroFilter</filter-name>
 		<!-- filter-class>org.apache.shiro.web.servlet.ShiroFilter</filter-class -->
 		<!-- 原生ShiroFilter,每次请求都会touch一次session,导致session持久化的时候压力非常大.ShiroFilter2能解决这个问题 -->
 		<filter-class>org.nutz.integration.shiro.ShiroFilter2</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>ShiroFilter</filter-name>
		<url-pattern>/*</url-pattern>
		<dispatcher>REQUEST</dispatcher>
		<dispatcher>FORWARD</dispatcher>
		<dispatcher>INCLUDE</dispatcher>
		<dispatcher>ERROR</dispatcher>
	</filter-mapping>
```

添加登陆用的入口方法
--------------------------

```java
    // 映射 /user/login , 与shiro.ini对应.
	@POST
	@Ok("json")
	@At
	public Object login(@Param("username")String username, 
					  @Param("password")String password,
					  @Param("rememberMe")boolean rememberMe,
					  @Param("captcha")String captcha) {
		NutMap re = new NutMap().setv("ok", false);
		// 如果已经登陆过,直接返回真
		Subject subject = SecurityUtils.getSubject();
		if (subject.isAuthenticated())
		    return re.setv("ok", true);
		// 检查用户名密码验证码是否正确,这里就不写出来了.
		// ............
		// 检查用户名密码
		User user = dao.fetch(User.class, username);
		if (user == null) {
			return re.setv("msg", "用户不存在");
		}
		// 比对密码, 严重建议用hash和加盐!!
		String face = new Sha256Hash(password, user.getSalt()).toHex();
		if (!face.equalsIgnoreCase(user.getPassword())) {
			return re.setv("msg", "密码错误");
		}
		subject.login(new SimpleShiroToken(user.getId()));
		// 需要放点东西进session,如果配置了ShiroSessionProvider,下面两种代码等价
		// req.getSession().setAttribute("me", user);
		// subject.getSession().setAttribute("me", user);
		return re.setv("ok", true);
	}
```

ShiroSessionProvider用法
--------------------------

在MainModule中的配置

```java
	@SessionBy(ShiroSessionProvider.class)
```

这个功能是可选,也是推荐的,配合ehcache/redis,可以实现session持久化

配置该SessionProvider后, nutz.mvc作用域内的req.getHttpSession均返回shiro的Session.

UU32SessionIdGenerator 用法
---------------------------

在shiro.ini内添加:

```java
    # use R.UU32()
    sessionIdGenerator = org.nutz.integration.shiro.UU32SessionIdGenerator
    securityManager.sessionManager.sessionDAO.sessionIdGenerator = $sessionIdGenerator
```


Session缓存与持久化
---------------------------

本插件在1.r.60.r2版开始集成了nutzcn验证过的Session持久化方案.

### LCache简介

LCache管理两层缓存,通常第一层为ehcache,第二层为redis, 使用redis的订阅发布机制实现集群同步.

之所以需要订阅发布机制,是因为ehcache通常是单机缓存,写入缓存时,需要通知其他机器清除对应的ehcache缓存,否则照常缓存不同步.

同步所需要的延迟,通常在可以接受范围.

### RedisCacheManager

可单独使用, 但建议作为LCache的二级缓存.

它支持两种模式 mode, 分别是kv和hset, 推荐使用kv模式.

kv模式, 使用set/get为主要方法, 命名方式 "cacheName:key", 能适应集群/分片环境, 在小数据量时,性能稍差

hset模式, 使用hset/hget为主要方法, 将同一个cache的数据,存放在同一个hset集合内, 数据量少的时候性能良好,但不可以用于集群和分片环境

它还有一个配置项, debug,可以输出详细的缓存读取/写入redis的情况,方便查错.


### 实例配置

以下配置取之nutzcn的shiro.ini

```ini
[main]

#Session
sessionManager = org.apache.shiro.web.session.mgt.DefaultWebSessionManager
### 禁用session有效性检查,可选
sessionManager.sessionValidationSchedulerEnabled = false

# Session Cache
sessionDAO = org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO
sessionManager.sessionDAO = $sessionDAO
securityManager.sessionManager = $sessionManager

### 声明2层缓存
jedisAgent = org.nutz.integration.jedis.JedisAgent
cacheManager_ehcache = org.apache.shiro.cache.ehcache.EhCacheManager
cacheManager_ehcache.cacheManagerConfigFile=classpath:ehcache.xml
cacheManager_redis = org.nutz.plugins.cache.impl.redis.RedisCacheManager
#RedisCacheManager 支持两种模式, hset和kv, 推荐使用kv模式. hset模式只适合数据量少的机器,更省内存.
cacheManager_redis.mode=kv
cacheManager_redis.debug=true
cacheManager = org.nutz.plugins.cache.impl.lcache.LCacheManager
cacheManager.level1 = $cacheManager_ehcache
cacheManager.level2 = $cacheManager_redis
cacheManager.jedisAgent = $jedisAgent
securityManager.cacheManager = $cacheManager
### 设置全局缓存实现
securityManager.cacheManager = $cacheManager
```

记得在@IocBy中启用jedis插件([传送门](https://github.com/nutzam/nutzmore/tree/master/nutz-integration-jedis))哦,否则上述配置挂哦. 

```java
		@IocBy(args={
			"*js", "ioc/",
			"*anno", "net.wendal.nutzbook",
			"*jedis"
			})
```

常见问题
---------------------------

**使用CaptchaFormAuthenticationFilter出现各种问题无法解决的话,请立即更换为SimpleAuthenticationFilter**

1. 使用CaptchaFormAuthenticationFilter, 账号密码均正确,但依然无法登陆 -- 必须带验证码,请查看其executeLogin方法
2. 使用CaptchaFormAuthenticationFilter,登陆就404,但事实上已经登陆 -- 若已经登陆,那么再次登陆时穿透的,如果后端没有入口方法对应,就会404.

3. 使用SimpleAuthenticationFilter, 就XXX -- 还没人遇到过问题,因为登陆操作在入口方法内,由你控制!!

### 在linux上shiro初始化很久

tomcat, 在setenv.sh添加如下

```
JAVA_OPTS=-Djava.security.egd=file:/dev/urandom ...其他配置
```

完整配置实例
------------------------------------------

```ini
[main]

#Session管理器,关闭定时校验机制,持久化环境下会非常耗内存
sessionManager = org.apache.shiro.web.session.mgt.DefaultWebSessionManager
sessionManager.sessionValidationSchedulerEnabled = false

#带缓存的SessionDAO
sessionDAO = org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO
sessionManager.sessionDAO = $sessionDAO
securityManager.sessionManager = $sessionManager

# use R.UU32(), 原生的是UUID,比较长
sessionIdGenerator = org.nutz.integration.shiro.UU32SessionIdGenerator
securityManager.sessionManager.sessionDAO.sessionIdGenerator = $sessionIdGenerator

# 2层缓存配置
jedisAgent = org.nutz.integration.jedis.JedisAgent
cacheManager_ehcache = org.apache.shiro.cache.ehcache.EhCacheManager
cacheManager_ehcache.cacheManagerConfigFile=classpath:ehcache.xml
cacheManager_redis = org.nutz.plugins.cache.impl.redis.RedisCacheManager
cacheManager_redis.mode=kv
cacheManager_redis.debug=true
cacheManager = org.nutz.plugins.cache.impl.lcache.LCacheManager
cacheManager.level1 = $cacheManager_ehcache
cacheManager.level2 = $cacheManager_redis
cacheManager.jedisAgent = $jedisAgent
securityManager.cacheManager = $cacheManager

# realm声明
nutzdao_realm = net.wendal.nutzbook.shiro.realm.SimpleAuthorizingRealm

# cookie, nutzcn使用超长时间的cookie,所以下面的timeout都很长
sessionIdCookie=org.apache.shiro.web.servlet.SimpleCookie
sessionIdCookie.name=sid
sessionIdCookie.maxAge=946080000
sessionIdCookie.httpOnly=true  
sessionManager.sessionIdCookie=$sessionIdCookie  
sessionManager.sessionIdCookieEnabled=true
sessionManager.globalSessionTimeout=946080000

authc = org.nutz.integration.shiro.SimpleAuthenticationFilter
authc.loginUrl  = /user/login
perms.loginUrl  = /user/login
roles.loginUrl  = /user/login
user.loginUrl   = /user/login
rest.loginUrl   = /user/login
logout.redirectUrl= /user/login


[urls]
/rs/*        = anon, noSessionCreation
/druid/*        = anon, noSessionCreation
/asserts/*        = anon, noSessionCreation
/user/logout = logout
/user/error  = anon
/user/count  = anon
```