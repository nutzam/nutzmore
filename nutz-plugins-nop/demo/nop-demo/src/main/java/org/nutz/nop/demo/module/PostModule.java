package org.nutz.nop.demo.module;

import java.util.Date;

import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.POST;
import org.nutz.nop.demo.module.GetModule.N;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

@Api(author = "Kerbores", name = "post请求", match = ApiMatchMode.ALL)
@At("post")
public class PostModule {

	@At
	@POST
	public NutMap simple() {
		return NutMap.NEW();
	}

	@At
	@POST
	public NutMap args(int i, String s, Date d) {
		return NutMap.NEW().addv("i", i).addv("s", s).addv("d", d);
	}

	@At
	@POST
	public NutMap array(int[] ids) {
		return NutMap.NEW().addv("ids", ids);
	}

	@At
	@POST
	public NutMap object(N n) {
		return NutMap.NEW().addv("n", n);
	}

	@At
	@POST
	@AdaptBy(type = JsonAdaptor.class)
	public NutMap body(N n) {
		return NutMap.NEW().addv("n", n);
	}

}
