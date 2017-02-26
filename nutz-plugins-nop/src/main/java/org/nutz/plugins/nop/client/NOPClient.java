package org.nutz.plugins.nop.client;

import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.plugins.nop.NOPConfig;
import org.nutz.plugins.nop.core.NOPRequest;
import org.nutz.plugins.nop.core.sign.DigestSigner;
import org.nutz.plugins.nop.core.sign.Signer;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file NopClient.java
 *
 * @description 客户端
 *
 * @time 2016年8月31日 下午2:54:27
 *
 */
public class NOPClient {

	private String appKey;
	private String appSecret;
	private String endpoint;// 调用点
	private String digestName;

	public String getDigestName() {
		return digestName;
	}

	public void setDigestName(String digestName) {
		this.digestName = digestName;
	}

	public Signer getSigner() {
		return new DigestSigner(digestName, null);
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	private NOPClient() {
	}

	public static NOPClient create(String appKey, String appSecret, String endpoint, String digestName) {
		NOPClient client = new NOPClient();
		client.setAppKey(appKey);
		client.setAppSecret(appSecret);
		client.setEndpoint(endpoint);
		client.setDigestName(digestName);
		return client;
	}

	public Response send(NOPRequest request) {
		request.setAppSecret(appSecret);
		request.getHeader().set(NOPConfig.tsKey, Times.now().getTime() + "");// 添加时间戳
		request.getHeader().set(NOPConfig.methodKey, request.getService());// 添加请求方法
		request.getHeader().set(NOPConfig.appkeyKey, appKey);// appKey
		request.getHeader().set("once", R.sg(16).next());
		String sign = getSigner().sign(request);
		request.getHeader().set(NOPConfig.signKey, sign);//签名
		
		Request req = null;
		if (request.getParams() != null) {
			req = Request.create(endpoint, request.getMethod(), request.getParams(), request.getHeader());
		} else {
			req = Request.create(endpoint, request.getMethod());
			req.setHeader(request.getHeader());
			req.setData(request.getData());
		}

		Sender sender = Sender.create(req);
		return sender.send();
	}
}
