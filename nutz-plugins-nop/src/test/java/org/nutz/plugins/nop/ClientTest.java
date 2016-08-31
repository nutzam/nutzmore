package org.nutz.plugins.nop;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.nutz.http.Header;
import org.nutz.http.Request.METHOD;
import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.nop.client.NOPClient;
import org.nutz.plugins.nop.core.NOPRequest;
import org.nutz.plugins.nop.core.serialize.UploadFile;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file ClientTest.java
 *
 * @description
 *
 * @time 2016年8月31日 下午8:46:03
 *
 */
public class ClientTest {
	NOPClient client;

	@Before
	public void init() {
		client = NOPClient.create("a", "b", "http://localhost:8080/ROOT/nop");
	}

	@Test
	public void post() {
		System.err.println(Json.toJson(client.send(NOPRequest.create("test/hello", METHOD.POST, NutMap.NEW(), Header.create())).getData()));
	}

	@Test
	public void calc() {
		System.err
				.println(Json.toJson(client.send(NOPRequest.create("test/calc", METHOD.POST, NutMap.NEW().addv("a", 5).addv("b", new int[] { 2, 3 }), Header.create())).getData()));
	}

	@Test
	public void file() {
		System.err.println(Json.toJson(NutMap.NEW().addv("a", 5).addv("b", new UploadFile(new File("/Users/ixion/git/SYL/platform/pom.xml")))));
		System.err
				.println(Json.toJson(client.send(
						NOPRequest.create("test/calc", METHOD.POST, NutMap.NEW().addv("a", 5).addv("b", new UploadFile(new File("/Users/ixion/git/SYL/platform/pom.xml"))),
								Header.create())).getData()));
	}
}
