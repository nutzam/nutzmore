package demo;

import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.json.Json;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.nop.client.NOPClient;
import org.nutz.plugins.nop.core.NOPRequest;

public class Demo {
	public static void main(String[] args) {

		NOPClient client = NOPClient.create("test", "tests", "http://open.songyoulai.com/nop.endpoint", "SHA1");//一般情况来说调用客户端一个是单例的
		Response response = client.send(NOPRequest.create("/ping", METHOD.POST).setData(Json.toJson(NutMap.NEW().addv("channelId", 2).addv("data", R.UU16()))));
		if (response.isOK()) {
			System.err.println(response.getContent());
		}

	}
}
