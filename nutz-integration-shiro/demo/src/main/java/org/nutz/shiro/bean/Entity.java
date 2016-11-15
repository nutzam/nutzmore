package org.nutz.shiro.bean;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

/**
 * @author admin
 *
 * @email kerbores@gmail.com
 *
 */
public class Entity {
	@Id
	private int id;

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Entity other = (Entity) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public int getId() {
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return Json.toJson(this, JsonFormat.compact());
	}
}
