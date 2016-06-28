package org.nutz.plugins.webqq.model;

import org.nutz.json.Json;
import org.nutz.json.JsonField;

/**
 * 讨论组
 * 
 * @author ScienJus
 * @date 2015/12/23.
 */
public class Discuss {

	@JsonField(value = "did")
	private long id;

	private String name;

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
