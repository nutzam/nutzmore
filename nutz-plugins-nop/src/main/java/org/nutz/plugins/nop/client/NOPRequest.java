package org.nutz.plugins.nop.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.nutz.http.Cookie;
import org.nutz.http.Header;
import org.nutz.http.Http;
import org.nutz.http.Request.METHOD;
import org.nutz.json.Json;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.Encoding;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.util.NutMap;

public class NOPRequest {

	public static NOPRequest get(String gateway) {
		return create(gateway, METHOD.GET, new HashMap<String, Object>());
	}

	public static NOPRequest get(String gateway, Header header) {
		return NOPRequest.create(gateway, METHOD.GET, new HashMap<String, Object>(), header);
	}

	public static NOPRequest post(String gateway) {
		return create(gateway, METHOD.POST, new HashMap<String, Object>());
	}

	public static NOPRequest post(String gateway, Header header) {
		return NOPRequest.create(gateway, METHOD.POST, new HashMap<String, Object>(), header);
	}

	public static NOPRequest create(String gateway, METHOD method) {
		return create(gateway, method, new HashMap<String, Object>());
	}

	@SuppressWarnings("unchecked")
	public static NOPRequest create(String gateway, METHOD method, String paramsAsJson, Header header) {
		return create(gateway, method, (Map<String, Object>) Json.fromJson(paramsAsJson), header);
	}

	@SuppressWarnings("unchecked")
	public static NOPRequest create(String gateway, METHOD method, String paramsAsJson) {
		return create(gateway, method, (Map<String, Object>) Json.fromJson(paramsAsJson));
	}

	public static NOPRequest create(String gateway, METHOD method, Map<String, Object> params) {
		return NOPRequest.create(gateway, method, params, Header.create());
	}

	public static NOPRequest create(String gateway, METHOD method, Map<String, Object> params, Header header) {
		return new NOPRequest().setMethod(method).setParams(params).setGateway(gateway).setHeader(header);
	}


	private NOPRequest() {
	}

	private String gateway;
	private METHOD method;
	private Header header;
	private Map<String, Object> params;
	private byte[] data;
	private InputStream inputStream;
	private String enc = Encoding.UTF8;

	/**
	 * @return the gateway
	 */
	public String getGateway() {
		return gateway;
	}

	/**
	 * @param gateway
	 *            the gateway to set
	 */
	public NOPRequest setGateway(String gateway) {
		this.gateway = gateway;
		return this;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public String getURLEncodedParams() {
		final StringBuilder sb = new StringBuilder();
		if (isFileUpload()) {// 文件上传的签名流
			List<String> keys = new ArrayList<String>(params.keySet());
			Collections.sort(keys);
			for (final String key : keys) {
				Object val = params.get(key);
				if (val == null)
					val = "";
				Lang.each(val, new Each<Object>() {
					@Override
					public void invoke(int index, Object ele, int length)
							throws ExitLoop, ContinueLoop, LoopException {
						if (ele instanceof File) {
							sb.append(Http.encode(key, enc))
									.append('=')
									.append(Http.encode(Lang.md5((File) ele), enc))
									.append('&');
						} else {
							sb.append(Http.encode(key, enc))
									.append('=')
									.append(Http.encode(ele, enc))
									.append('&');
						}
					}
				});
			}
		} else if (params != null) {
			for (Entry<String, Object> en : params.entrySet()) {
				final String key = en.getKey();
				Object val = en.getValue();
				if (val == null)
					val = "";
				Lang.each(val, new Each<Object>() {
					@Override
					public void invoke(int index, Object ele, int length)
							throws ExitLoop, ContinueLoop, LoopException {
						sb.append(Http.encode(key, enc))
								.append('=')
								.append(Http.encode(ele, enc))
								.append('&');
					}
				});
			}
		}
		if (sb.length() > 0)
			sb.setLength(sb.length() - 1);
		return sb.toString();
	}

	public boolean isFileUpload() {
		final NutMap t = NutMap.NEW().addv("target", false);
		if ((isPost() || isPut()) && getParams() != null) {
			for (Object val : getParams().values()) {
				Lang.each(val, new Each<Object>() {

					@Override
					public void invoke(int index, Object ele, int length) throws ExitLoop, ContinueLoop, LoopException {
						if (ele instanceof File) {
							t.put("target", true);
							throw new ExitLoop();
						}
					}
				});
			}
		}
		return t.getBoolean("target");
	}

	public InputStream getInputStream() {
		if (inputStream != null) {
			return inputStream;
		} else {
			if (header.get("Content-Type") == null)
				header.asFormContentType(enc);
			if (null == data) {
				try {
					return new ByteArrayInputStream(getURLEncodedParams().getBytes(enc));
				} catch (UnsupportedEncodingException e) {
					throw Lang.wrapThrow(e);
				}
			}
			return new ByteArrayInputStream(data);
		}
	}

	public NOPRequest setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		return this;
	}

	public byte[] getData() {
		return data;
	}

	public NOPRequest setData(byte[] data) {
		this.data = data;
		return this;
	}

	public NOPRequest setData(String data) {
		try {
			this.data = data.getBytes(Encoding.UTF8);
		} catch (UnsupportedEncodingException e) {
			// 不可能
		}
		return this;
	}

	public NOPRequest setParams(Map<String, Object> params) {
		this.params = params;
		return this;
	}

	public METHOD getMethod() {
		return method;
	}

	public boolean isGet() {
		return METHOD.GET == method;
	}

	public boolean isPost() {
		return METHOD.POST == method;
	}

	public boolean isDelete() {
		return METHOD.DELETE == method;
	}

	public boolean isPut() {
		return METHOD.PUT == method;
	}

	public NOPRequest setMethod(METHOD method) {
		this.method = method;
		return this;
	}

	public Header getHeader() {
		return header;
	}

	public NOPRequest setHeader(Header header) {
		if (header == null)
			header = Header.create();
		this.header = header;
		return this;
	}

	public NOPRequest setCookie(Cookie cookie) {
		header.set("Cookie", cookie.toString());
		return this;
	}

	public Cookie getCookie() {
		String s = header.get("Cookie");
		if (null == s)
			return new Cookie();
		return new Cookie(s);
	}

	/**
	 * 设置发送内容的编码,仅对String或者Map<String,Object>类型的data有效
	 */
	public NOPRequest setEnc(String reqEnc) {
		if (reqEnc != null)
			this.enc = reqEnc;
		return this;
	}

	public String getEnc() {
		return enc;
	}

	public NOPRequest header(String key, String value) {
		getHeader().set(key, value);
		return this;
	}
}
