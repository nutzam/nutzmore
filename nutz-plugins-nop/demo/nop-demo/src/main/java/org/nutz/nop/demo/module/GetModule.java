package org.nutz.nop.demo.module;

import java.util.Date;

import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

@Api(author = "Kerbores", name = "get请求", match = ApiMatchMode.ALL)
@At("get")
public class GetModule {

	public static class N {
		private int id;
		private String name;
		private Date birth;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Date getBirth() {
			return birth;
		}

		public void setBirth(Date birth) {
			this.birth = birth;
		}

	}

	@At
	public NutMap simple() {
		return NutMap.NEW().addv("msg", "Hello Nop!");
	}

	@At
	public NutMap args(int i, String s, Date d) {
		return NutMap.NEW().addv("i", i).addv("s", s).addv("d", d);
	}

	@At
	public NutMap array(int[] ids) {
		return NutMap.NEW().addv("ids", ids);
	}

	@At
	public NutMap object(N n) {
		return NutMap.NEW().addv("n", n);
	}

}
