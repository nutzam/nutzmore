package org.nutz.plugins.nop.client;

import org.nutz.http.Header;
import org.nutz.http.Request;
import org.nutz.http.Request.METHOD;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.nop.NOPConfig;
import org.nutz.plugins.nop.core.sign.DigestSigner;

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

	Log log = Logs.get();

	public static NOPClient create(String appKey, String appSecret, String endpoint, String digestName) {
		NOPClient client = new NOPClient();
		client.setAppKey(appKey);
		client.setAppSecret(appSecret);
		client.setEndpoint(endpoint);
		client.setDigestName(digestName);
		return client;
	}

	public Request toRequest(NOPRequest request) {
		Request req = Request.create(endpoint, request.getMethod());
		req.setParams(request.getParams());
		req.setData(request.getData());
		req.setHeader(signHeader(request));
		Header header = req.getHeader();
		log.debugf("send headers %s", header);
		return req;
	}

	/**
	 * 发送请求
	 * 
	 * @param request
	 * @return
	 */
	public Response send(NOPRequest request) {
		return Sender.create(toRequest(request)).send();
	}

	/**
	 * 处理header
	 * 
	 * @param request
	 * @return
	 */
	private Header signHeader(NOPRequest request) {
		String nonce = R.UU16();
		String ts = Times.now().getTime() + "";
		Header header = null;
		if (request.getMethod() == METHOD.GET) {
			String query = request.getURLEncodedParams();
			String method = Strings.isBlank(query) ? request.getGateway() : request.getGateway() + "?" + query;
			header = request.getHeader()
					.set(NOPConfig.appkeyKey, appKey)
					.set(NOPConfig.methodKey, method)
					.set(NOPConfig.nonceKey, nonce)
					.set(NOPConfig.tsKey, ts)
					.set(NOPConfig.signKey, new DigestSigner(digestName).sign(appSecret, ts, method, nonce, request));
		} else {
			header = request.getHeader()
					.set(NOPConfig.appkeyKey, appKey)
					.set(NOPConfig.methodKey, request.getGateway())
					.set(NOPConfig.nonceKey, nonce)
					.set(NOPConfig.tsKey, ts)
					.set(NOPConfig.signKey, new DigestSigner(digestName).sign(appSecret, ts, request.getGateway(), nonce, request));
		}
		return request.getData() == null || request.getData().length == 0 ? header.asFormContentType() : header.asJsonContentType();
	}
}
