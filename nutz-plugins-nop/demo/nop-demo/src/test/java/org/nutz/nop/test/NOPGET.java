package org.nutz.nop.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Test;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.nop.client.NOPRequest;

public class NOPGET extends Base {

	@Test
	public void simple() {
		Response response = client.send(NOPRequest.create("/get/simple", METHOD.GET));
		if (response.isOK()) {
			System.err.println(response.getContent());
			assertNotNull(response.getContent());
		}
	}

	@Test
	public void args() {
		int i = R.random(0, 100);
		String s = R.sg(10).next() + "中文";
		Date d = Times.now();
		Response response = client.send(NOPRequest.create("/get/args", METHOD.GET, NutMap.NEW().addv("i", i).addv("s", s).addv("d", Times.format("yyyy-MM-dd HH:mm:ss", d))));
		if (response.isOK()) {
			NutMap data = Lang.map(response.getContent());
			System.err.println(data);
			assertEquals(i, data.getInt("i"));
			assertEquals(s, data.getString("s"));
			assertEquals(Times.format("yyyy-MM-dd HH:mm:ss", d), Times.format("yyyy-MM-dd HH:mm:ss", data.getTime("d")));
		}
	}

	@Test
	public void array() {
		Integer[] ids = Lang.array(R.random(0, 100), R.random(0, 100), R.random(0, 100), R.random(0, 100));
		Response response = client.send(NOPRequest.create("/get/array", METHOD.GET, NutMap.NEW().addv("ids", ids)));
		if (response.isOK()) {
			NutMap data = Lang.map(response.getContent());
			System.err.println(data);
			assertEquals(ids.length, data.getArray("ids", Integer.class).length);
		}
	}

	@Test
	public void object() {
		NutMap data = NutMap.NEW().addv("id", 1).addv("name", "Kerbores").addv("birth", Times.now());
		Response response = client.send(NOPRequest.create("/get/object", METHOD.GET, NutMap.NEW().addv("n", Json.toJson(data, JsonFormat.compact()))));
		if (response.isOK()) {
			NutMap temp = Lang.map(response.getContent());
			System.err.println(temp);
		}
	}

}
