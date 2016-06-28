package org.nutz.plugins.webqq.model;

import java.util.ArrayList;
import java.util.List;

import org.nutz.json.Json;
import org.nutz.json.JsonField;

/**
 * 
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-webqq
 *
 * @file DiscussInfo.java
 *
 * @description 讨论组资料
 *
 *
 * @DateTime 2016年6月28日 下午1:28:16
 *
 */
public class DiscussInfo {

	@JsonField(value = "did")
	private long id;

	@JsonField(value = "discu_name")
	private String name;

	private List<DiscussUser> users = new ArrayList<DiscussUser>();

	public void addUser(DiscussUser user) {
		this.users.add(user);
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the users
	 */
	public List<DiscussUser> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public void setUsers(List<DiscussUser> users) {
		this.users = users;
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
