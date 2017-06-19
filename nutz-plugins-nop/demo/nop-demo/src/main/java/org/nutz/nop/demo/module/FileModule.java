package org.nutz.nop.demo.module;

import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.upload.TempFile;
import org.nutz.mvc.upload.UploadAdaptor;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

@Api(author = "Kerbores", name = "文件上传", match = ApiMatchMode.ALL)
@At("file")
public class FileModule {

	@At
	@POST
	@AdaptBy(type = UploadAdaptor.class)
	public NutMap simple(TempFile file) {
		return NutMap.NEW().addv("name", file.getSubmittedFileName());
	}

	@At
	@POST
	@AdaptBy(type = UploadAdaptor.class)
	public NutMap arrays(TempFile[] files) {
		return NutMap.NEW().addv("name", files.length);
	}

	@At
	@POST
	@AdaptBy(type = UploadAdaptor.class)
	public NutMap args(TempFile file, int id) {
		return NutMap.NEW().addv("name", file.getSubmittedFileName()).addv("id", id);
	}

}
