package demo;

import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.nop.client.NOPClient;
import org.nutz.plugins.nop.core.NOPRequest;

public class Demo {
	public static void main(String[] args) {
		NOPClient client = NOPClient.create("test", Lang.md5("test"), "http://localhost:8080/nop-demo/nop.endpoint", "SHA1");// 一般情况来说调用客户端一个是单例的
		Response response = client
				.send(NOPRequest.create("/b/ss", METHOD.POST)
						.setParams(NutMap.NEW().addv("i", Lang.array(R.random(0, 10), R.random(0, 10), R.random(0, 10), R.random(0, 10))).addv("k", "中文").addv("d",
								Times.format("yyyy-MM-dd", Times.now()))));
		if (response.isOK()) {
			System.err.println(response.getContent());
		}
		// response = client
		// .send(NOPRequest.create("/b/ss?i=0&k=%E4%B8%AD%E6%96%87&d=2017-04-14", METHOD.GET);
//		if (response.isOK()) {
//			System.err.println(response.getContent());
//		}
	}
}
