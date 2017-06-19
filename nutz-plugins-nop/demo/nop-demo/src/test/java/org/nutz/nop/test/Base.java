package org.nutz.nop.test;

import org.junit.Before;
import org.nutz.lang.Lang;
import org.nutz.plugins.nop.client.NOPClient;

public class Base {

	protected NOPClient client;

	@Before
	public void init() {
		/**
		 * 1.调用点本机 <br/>
		 * 2.服务器端实现的appSecret仅仅是取appKey的md5,生成环境可能是从数据库获取的 <br/>
		 * 3.签名算法使用的是SHA1 <br/>
		 */
		client = NOPClient.create("test", Lang.md5("test"), "http://localhost:8080/nop-demo/nop.endpoint", "SHA1");
	}

}
