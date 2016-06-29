package org.nutz.plugins.webqq.model;

import org.nutz.json.Json;

/**
 * 
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-webqq
 *
 * @file DiscussMessage.java
 *
 * @description 讨论组消息
 *
 *
 * @DateTime 2016年6月28日 下午1:28:33
 *
 */
public class DiscussMessage {

	private long discussId;

	private long time;

	private String content;

	private long userId;

	private Font font;

	/**
	 * @return the discussId
	 */
	public long getDiscussId() {
		return discussId;
	}

	/**
	 * @param discussId
	 *            the discussId to set
	 */
	public void setDiscussId(long discussId) {
		this.discussId = discussId;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * @return the font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * @param font
	 *            the font to set
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	public DiscussMessage(Object obj) {
		// JSONArray content = json.getJSONArray("content");
		// this.font = content.getJSONArray(0).getObject(1, Font.class);
		// this.content = content.getString(1);
		// this.time = json.getLongValue("time");
		// this.discussId = json.getLongValue("did");
		// this.userId = json.getLongValue("send_uin");
		System.err.println(Json.toJson(obj));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Json.toJson(this);
	}

}
