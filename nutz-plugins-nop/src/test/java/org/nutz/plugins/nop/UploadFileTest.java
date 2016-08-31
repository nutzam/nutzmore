package org.nutz.plugins.nop;

import java.io.File;

import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.lang.Files;
import org.nutz.plugins.nop.core.serialize.UploadFile;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file UploadFileTest.java
 *
 * @description
 *
 * @time 2016年8月31日 下午6:41:01
 *
 */
public class UploadFileTest {

	@Test
	public void json() {
		UploadFile ufo = new UploadFile(new File("/Users/ixion/git/SYL/platform/pom.xml"));
		System.err.println(ufo.getName());
		String json = Json.toJson(ufo);// 序列化

		System.err.println(json);// 看看序列化结果

		File f = ufo.serialize(json).getFile();// 反序列化

		System.err.println(Files.read(f));// 看下文件内容
		System.err.println(f.getName());// 看下文件名
	}
}
