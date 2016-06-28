package org.nutz.plugins.webqq.model;

import org.nutz.json.Json;

/**
 * 群成员
 * 
 * @author ScienJus
 * @date 2015/12/24.
 */
public class GroupUser {

	private String nick;

	private String province;

	private String gender;

	private long uin;

	private String country;

	private String city;

	private String card;

	private int clientType;

	private int status;

	private boolean vip;

	private int vipLevel;

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
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}

	/**
	 * @param province
	 *            the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
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
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * @param country
	 *            the country to set
	 */
	public void setCountry(String country) {
		this.country = country;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}

	/**
	 * @param city
	 *            the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * @return the card
	 */
	public String getCard() {
		return card;
	}

	/**
	 * @param card
	 *            the card to set
	 */
	public void setCard(String card) {
		this.card = card;
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
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
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

}
