Nutz集成Shiro的插件
======================

简介(可用性:生产)
==================================

集成Shiro的登陆,鉴权,和Session机制

本插件分成多个部分
-------------------------

* NutShiroProcessor -- 动作链处理器
* CaptchaFormAuthenticationFilter及CaptchaUsernamePasswordToken -- 带验证码的登陆拦截器
* NutDaoRealm -- 供使用者参考的Realm实现,因为其需要使用者适配自己的权限模型.


使用方法
-------------------------

* 添加shiro的jar, 支持2.x版本,建议用最新版
* 在src或maven的resources目录下添加一个 shiro.ini
* 在src或maven的resources下添加一个mvc-chain.js引用NutShiroProcessor
* 在MainModule中引用mvc-chain.js

供参考的shiro.ini
--------------------------

其中的net.wendal.nutzbook.shiro.realm.NutDaoRealm是nutzbook中的NutDaoRealm实现.

	[main]
	sha256Matcher = org.apache.shiro.authc.credential.Sha256CredentialsMatcher
	nutzdao_realm = net.wendal.nutzbook.shiro.realm.NutDaoRealm
	nutzdao_realm.credentialsMatcher = $sha256Matcher

	authc = org.nutz.integration.shiro.CaptchaFormAuthenticationFilter
	authc.loginUrl  = /user/login
	logout.redirectUrl= /user/login

	[urls]
	/rs/*        = anon
	/user/logout = logout
	
供参考的mvc-chain.js
---------------------------
	var chain={
	"default" : {
		"ps" : [
		      "org.nutz.mvc.impl.processor.UpdateRequestAttributesProcessor",
		      "org.nutz.mvc.impl.processor.EncodingProcessor",
		      "org.nutz.mvc.impl.processor.ModuleProcessor",
		      "org.nutz.integration.shiro.NutShiroProcessor",
		      "org.nutz.mvc.impl.processor.ActionFiltersProcessor",
		      "org.nutz.mvc.impl.processor.AdaptorProcessor",
		      "org.nutz.mvc.impl.processor.MethodInvokeProcessor",
		      "org.nutz.mvc.impl.processor.ViewProcessor"
		      ],
		"error" : 'org.nutz.mvc.impl.processor.FailProcessor'
	}
	};
	
MainModule中的配置
--------------------------

	@ChainBy(args="mvc-chain.js")