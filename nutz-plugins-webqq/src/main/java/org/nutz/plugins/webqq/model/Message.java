package org.nutz.plugins.webqq.model;

import org.nutz.json.Json;
import org.nutz.lang.util.NutMap;

/**
 * 
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-webqq
 *
 * @file Message.java
 *
 * @description 消息.
 *
 *
 * @DateTime 2016年6月28日 下午1:30:10
 *
 */
public class Message {

	private long time;

	private String content;

	private long userId;

	private Font font;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Json.toJson(this);
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

	public Message(Object obj) {

		NutMap map = NutMap.WRAP(Json.toJson(obj));
		System.err.println(map);
		map.getList("content", NutMap.class);

		// JSONArray cont = json.getJSONArray("content");
		// this.font = cont.getJSONArray(0).getObject(1, Font.class);
		//
		// final int size = cont.size();
		// final StringBuilder contentBuilder = new StringBuilder();
		// for (int i = 1; i < size; i++) {
		// contentBuilder.append(cont.getString(i));
		// }
		// this.content = contentBuilder.toString();
		//
		// this.time = json.getLongValue("time");
		// this.userId = json.getLongValue("from_uin");

	}

}
