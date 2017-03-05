package org.nutz.apidoc.demo.module;

import java.lang.reflect.Field;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.Mirror;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.ReturnKey;


@At("b")
@Api(name = "B", match = ApiMatchMode.ALL)
public class B {

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
	

	@AdaptBy(type = JsonAdaptor.class)
	@At
	@Api(name="NNN",ok={
			@ReturnKey(key="id",description="N-id"),
			@ReturnKey(key="name",description="名称"),
			@ReturnKey(key="birth",description="生日"),
	})
	public N kkk(N n) {
		return n;
	}
	
	public static void main(String[] args) {
		for (Field f : Mirror.me(NutMap.class).getFields()) {
			System.err.println(f.getName() +":" + f.getType().getName());
		}
	}

	@At
	public NutMap name(HttpServletRequest request) {
		return NutMap.NEW();
	}
	
	@At
	public NutMap ss(int i,Date d) {
		return NutMap.NEW();
	}

}
