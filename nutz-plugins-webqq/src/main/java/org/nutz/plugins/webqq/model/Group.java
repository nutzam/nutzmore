package org.nutz.plugins.webqq.model;

import org.nutz.json.Json;
import org.nutz.json.JsonField;

/**
 * 群
 * 
 * @author ScienJus
 * @date 2015/12/18.
 */
public class Group {

	@JsonField(value = "gid")
	private long id;

	private String name;

	private long flag;

	private long code;

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
	 * @return the flag
	 */
	public long getFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(long flag) {
		this.flag = flag;
	}

	/**
	 * @return the code
	 */
	public long getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(long code) {
		this.code = code;
	}

}
