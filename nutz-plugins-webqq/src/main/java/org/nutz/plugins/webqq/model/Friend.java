package org.nutz.plugins.webqq.model;

import org.nutz.json.Json;

/**
 * 好友
 * 
 * @author ScienJus
 * @date 2015/12/18.
 */
public class Friend {

	private long userId;

	private String markname = "";

	private String nickname;

	private boolean vip;

	private int vipLevel;

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
	 * @return the markname
	 */
	public String getMarkname() {
		return markname;
	}

	/**
	 * @param markname
	 *            the markname to set
	 */
	public void setMarkname(String markname) {
		this.markname = markname;
	}

	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @param nickname
	 *            the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the vip
	 */
	public boolean isVip() {
		return vip;
	}

	/**
	 * @param vip
	 *            the vip to set
	 */
	public void setVip(boolean vip) {
		this.vip = vip;
	}

	/**
	 * @return the vipLevel
	 */
	public int getVipLevel() {
		return vipLevel;
	}

	/**
	 * @param vipLevel
	 *            the vipLevel to set
	 */
	public void setVipLevel(int vipLevel) {
		this.vipLevel = vipLevel;
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
