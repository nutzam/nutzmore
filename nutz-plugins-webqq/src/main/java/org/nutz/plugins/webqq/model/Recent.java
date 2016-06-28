package org.nutz.plugins.webqq.model;

import org.nutz.json.Json;

/**
 * 最近会话
 * 
 * @author ScienJus
 * @date 2015/12/24.
 */
public class Recent {

	private long uin;

	// 0:好友、1:群、2:讨论组
	private int type;

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
	 * @return the uin
	 */
	public long getUin() {
		return uin;
	}

	/**
	 * @param uin
	 *            the uin to set
	 */
	public void setUin(long uin) {
		this.uin = uin;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

}
