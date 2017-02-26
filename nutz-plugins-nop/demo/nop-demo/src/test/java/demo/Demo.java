package demo;

import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.lang.Lang;
import org.nutz.plugins.nop.client.NOPClient;
import org.nutz.plugins.nop.core.NOPRequest;

public class Demo {
	public static void main(String[] args) {
		
		NOPClient client = NOPClient.create("abcd", Lang.md5("abcd"), "localhost:8080/nop-demo/endpoint", "MD5");
		Response response = client.send(NOPRequest.create("/", METHOD.GET));
		if (response.isOK()) {
			System.err.println(response.getContent());
		}

	}
}
