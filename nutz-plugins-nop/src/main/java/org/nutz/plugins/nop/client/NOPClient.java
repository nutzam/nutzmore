package org.nutz.plugins.nop.client;

import org.nutz.http.Request;
import org.nutz.http.Sender;
import org.nutz.json.Json;
import org.nutz.lang.Times;
import org.nutz.lang.util.NutMap;
import org.nutz.plugins.nop.NOPConfig;
import org.nutz.plugins.nop.core.NOPRequest;
import org.nutz.plugins.nop.core.NOPResponse;
import org.nutz.plugins.nop.core.sign.NOPSigner;
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

	private Signer signer = new NOPSigner();

	public String getAppKey() {
		return appKey;
	}

	public Signer getSigner() {
		return signer;
	}

	public void setSigner(Signer signer) {
		this.signer = signer;
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

	public static NOPClient create(String appKey, String appSecret, String endpoint) {
		NOPClient client = new NOPClient();
		client.setAppKey(appKey);
		client.setAppSecret(appSecret);
		client.setEndpoint(endpoint);
		return client;
	}

	public NOPResponse send(NOPRequest request) {
		request.getHeader().set(NOPConfig.tskey(), Times.now().getTime() + "");// 添加时间戳
		request.getHeader().set(NOPConfig.methodKey(), request.getService());// 添加请求方法
		String sign = signer.sign(appKey, appSecret, request);

		request.getHeader().set(NOPConfig.signkey(), sign);
		request.getHeader().set(NOPConfig.signerKey(), signer.name());
		request.getHeader().set(NOPConfig.appkeyKey(), appKey);// appKey
		request.getHeader().set(NOPConfig.appSecretKey(), appSecret);// appSecret

		Request req = Request.create(endpoint, request.getMethod(), NutMap.NEW(), request.getHeader()).setData(Json.toJson(request.getParams()));

		Sender sender = Sender.create(req);
		return NOPResponse.warp(sender.send());
	}
}
