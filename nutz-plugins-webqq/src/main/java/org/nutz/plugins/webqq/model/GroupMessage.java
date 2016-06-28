package org.nutz.plugins.webqq.model;

import org.nutz.json.Json;

/**
 * 群消息.
 *
 * @author ScienJus
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @date 15/12/19.
 */
public class GroupMessage {

	private long groupId;

	private long time;

	private String content;

	private long userId;

	private Font font;

	public GroupMessage(Object obj) {
		System.err.println(Json.toJson(obj));
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
		// this.groupId = json.getLongValue("group_code");
		// this.userId = json.getLongValue("send_uin");
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

	/**
	 * @return the groupId
	 */
	public long getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(long groupId) {
		this.groupId = groupId;
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

}
