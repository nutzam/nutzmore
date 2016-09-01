package org.nutz.plugins.nop.core;

import org.nutz.http.Cookie;
import org.nutz.http.Header;
import org.nutz.http.Response;
import org.nutz.json.Json;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file NOPResponse.java
 *
 * @description 响应
 *
 * @time 2016年8月31日 下午3:07:02
 *
 */
public class NOPResponse {

	private Header header;

	private Cookie cookie;

	private String protocal = "HTTP/1.1";

	private int status;

	private String detail;

	private String content;

	public static NOPResponse warp(Response response) {
		NOPResponse warp = new NOPResponse();
		warp.setContent(response.getContent());
		warp.setCookie(response.getCookie());
		warp.setDetail(response.getDetail());
		warp.setHeader(response.getHeader());
		warp.setProtocal(response.getProtocal());
		warp.setStatus(response.getStatus());
		return warp;
	}

	public NOPData getData() {
		try {
			return Json.fromJson(NOPData.class, content);
		} catch (Exception e) {
			return NOPData.exception(e);
		}

	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public Cookie getCookie() {
		return cookie;
	}

	public void setCookie(Cookie cookie) {
		this.cookie = cookie;
	}

	public String getProtocal() {
		return protocal;
	}

	public void setProtocal(String protocal) {
		this.protocal = protocal;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
