package org.nutz.plugins.nop;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.http.Header;
import org.nutz.http.Request.METHOD;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.nop.core.NOPRequest;
import org.nutz.plugins.nop.core.sign.NOPSigner;
import org.nutz.plugins.nop.core.sign.Signer;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file SignerTest.java
 *
 * @description
 *
 * @time 2016年8月31日 下午7:26:12
 *
 */
public class SignerTest {

	@Test
	public void sign() {
		Signer signer = new NOPSigner();
		NutMap paras = NutMap.NEW().addv("t", 1).addv("a", 1);
		Header header = Header.create().clear().set("ka", "1").set("ta", "2");
		NOPRequest request = NOPRequest.create("/test", METHOD.POST, paras, header);

		System.err.println(Json.toJson(request.getHeader()));

		Assert.assertEquals(signer.sign("a", "b", request), Lang.sha1("aa1t1ka1ta2b"));// Header
	}
}
