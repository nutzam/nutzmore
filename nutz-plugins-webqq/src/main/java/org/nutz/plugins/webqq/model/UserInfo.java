package org.nutz.plugins.webqq.model;

import org.nutz.json.Json;
import org.nutz.json.JsonField;

/**
 * 用户
 * 
 * @author ScienJus
 * @date 2015/12/24.
 */
public class UserInfo {

	private Birthday birthday;

	private String phone;

	private String occupation;

	private String college;

	private String uin;

	private int blood;

	private String lnick; // 签名

	private String homepage;

	@JsonField(value = "vip_info")
	private int vipInfo;

	private String city;

	private String country;

	private String province;

	private String personal;

	private int shengxiao;

	private String nick;

	private String email;

	private String account;

	private String gender;

	private String mobile;

	/**
	 * @return the birthday
	 */
	public Birthday getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday
	 *            the birthday to set
	 */
	public void setBirthday(Birthday birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 *            the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the occupation
	 */
	public String getOccupation() {
		return occupation;
	}

	/**
	 * @param occupation
	 *            the occupation to set
	 */
	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	/**
	 * @return the college
	 */
	public String getCollege() {
		return college;
	}

	/**
	 * @param college
	 *            the college to set
	 */
	public void setCollege(String college) {
		this.college = college;
	}

	/**
	 * @return the uin
	 */
	public String getUin() {
		return uin;
	}

	/**
	 * @param uin
	 *            the uin to set
	 */
	public void setUin(String uin) {
		this.uin = uin;
	}

	/**
	 * @return the blood
	 */
	public int getBlood() {
		return blood;
	}

	/**
	 * @param blood
	 *            the blood to set
	 */
	public void setBlood(int blood) {
		this.blood = blood;
	}

	/**
	 * @return the lnick
	 */
	public String getLnick() {
		return lnick;
	}

	/**
	 * @param lnick
	 *            the lnick to set
	 */
	public void setLnick(String lnick) {
		this.lnick = lnick;
	}

	/**
	 * @return the homepage
	 */
	public String getHomepage() {
		return homepage;
	}

	/**
	 * @param homepage
	 *            the homepage to set
	 */
	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	/**
	 * @return the vipInfo
	 */
	public int getVipInfo() {
		return vipInfo;
	}

	/**
	 * @param vipInfo
	 *            the vipInfo to set
	 */
	public void setVipInfo(int vipInfo) {
		this.vipInfo = vipInfo;
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
	 * @return the personal
	 */
	public String getPersonal() {
		return personal;
	}

	/**
	 * @param personal
	 *            the personal to set
	 */
	public void setPersonal(String personal) {
		this.personal = personal;
	}

	/**
	 * @return the shengxiao
	 */
	public int getShengxiao() {
		return shengxiao;
	}

	/**
	 * @param shengxiao
	 *            the shengxiao to set
	 */
	public void setShengxiao(int shengxiao) {
		this.shengxiao = shengxiao;
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
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email
	 *            the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
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
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile
	 *            the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
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
