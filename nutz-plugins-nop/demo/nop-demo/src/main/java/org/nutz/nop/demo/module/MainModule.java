package org.nutz.nop.demo.module;

import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.UrlMappingBy;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.plugins.apidoc.ApidocUrlMapping;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.Manual;
import org.nutz.plugins.nop.server.NOPSignFilter;

@IocBy(type = ComboIocProvider.class, args = { "*anno", "org.nutz.nop", "*async" })
@Ok("json")
@UrlMappingBy(ApidocUrlMapping.class)
@Manual(name = "NOP示例手册", description = "Nop集成的Apidoc手册", author = "Kerbores", email = "kerbores@gmail.com", homePage = "http://blog.zhcs.club", copyRight = "️ &copy; 2017 Kerbores All Right XXX")
@Api(author = "Kerbores", name = "主模块", match = ApiMatchMode.ALL)
@Filters(@By(type = NOPSignFilter.class, args = { "SHA1" }))
public class MainModule {

	@At("/")
	@Filters
	public NutMap hello() {
		return NutMap.NEW().addv("msg", "Hello NOP!");
	}

}
