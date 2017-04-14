package org.nutz.nop.demo.module;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.util.NutMap;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;
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

	@At
	public NutMap name(HttpServletRequest request) {
		return NutMap.NEW();
	}
	
	@At
	public NutMap ss(int[] i, Date d, String k) {
		return NutMap.NEW().addv("i", i).addv("d", d).addv("k", k);
	}

	@At
	@AdaptBy(type = UploadAdaptor.class)
	public NutMap upload(TempFile[] file) {
		return NutMap.NEW().addv("f", file.length);
	}

}
