package org.nutz.plugins.webqq.model;

import org.nutz.json.Json;

/**
 * 
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-webqq
 *
 * @file DiscussUser.java
 *
 * @description 讨论组成员
 *
 *
 * @DateTime 2016年6月28日 下午1:28:47
 *
 */
public class DiscussUser {

	private long uin;

	private String nick;

	private int clientType;

	private String status;

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
	 * @return the nick
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * @param nick
	 *            the nick to set
	 */
	public void setNick(String nick) {
		this.nick = nick;
	}

	/**
	 * @return the clientType
	 */
	public int getClientType() {
		return clientType;
	}

	/**
	 * @param clientType
	 *            the clientType to set
	 */
	public void setClientType(int clientType) {
		this.clientType = clientType;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
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
