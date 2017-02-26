package org.nutz.apidoc.demo.module;

import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.UrlMappingBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.plugins.apidoc.ApidocUrlMapping;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.Manual;

@Modules
@IocBy(type = ComboIocProvider.class, args = { "*anno", "org.nutz.apidoc", "*async" })
@Ok("json")
@UrlMappingBy(ApidocUrlMapping.class)
@Manual(name="测试",description="scadcew",author="Kerbores",email="kerbores@gmail.com",homePage="http://www.baidu.com",copyRight="️ &copy; 2017 Kerbores All Right XXX")
@Api(author="kkk",name="KKK",match=ApiMatchMode.ALL)
public class MainModule {

	@At
	@Api(name = "index",description="我是描述字段")
	public NutMap index() {
		return NutMap.NEW().addv("msg", "Hello Nutz!");
	}

	@At
	@AdaptBy(type = JsonAdaptor.class)
	public NutMap name(NutMap data) {
		return NutMap.NEW().addv("msg", "Hello Nutz!");
	}

}
