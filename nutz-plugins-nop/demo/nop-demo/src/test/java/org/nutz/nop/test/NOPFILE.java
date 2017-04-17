package org.nutz.nop.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.nop.client.NOPRequest;

public class NOPFILE extends Base {

	@Test
	public void simple() {
		Response response = client.send(NOPRequest.create("/file/simple", METHOD.POST, NutMap.NEW().addv("file", Files.checkFile("C:/qqcywifi.ini"))));
		if (response.isOK()) {
			System.err.println(response.getContent());
			assertNotNull(response.getContent());
		}
	}

	@Test
	public void array() {
		Response response = client.send(
				NOPRequest.create("/file/arrays", METHOD.POST, NutMap.NEW().addv("files", Lang.array(Files.checkFile("C:/qqcywifi.ini"), Files.checkFile("C:/wifiname.txt")))));
		if (response.isOK()) {
			System.err.println(response.getContent());
			assertNotNull(response.getContent());
		}
	}

	@Test
	public void args() {
		Response response = client.send(
				NOPRequest.create("/file/args", METHOD.POST, NutMap.NEW().addv("file", Files.checkFile("C:/qqcywifi.ini")).addv("id", 10)));
		if (response.isOK()) {
			System.err.println(response.getContent());
			assertNotNull(response.getContent());
		}
	}
}
