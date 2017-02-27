package demo;

import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.plugins.nop.client.NOPClient;
import org.nutz.plugins.nop.core.NOPRequest;

public class Demo {
	public static void main(String[] args) {

		NOPClient client = NOPClient.create("abcd", Lang.md5("abcd"), "http://localhost:8080/nop-demo/nop.endpoint", "SHA1");
		Response response = client.send(NOPRequest.create("/body", METHOD.POST).setData(Json.toJson(Lang.map("{id:1,name:'kkk'}"))));
		if (response.isOK()) {
			System.err.println(response.getContent());
		}

	}
}
