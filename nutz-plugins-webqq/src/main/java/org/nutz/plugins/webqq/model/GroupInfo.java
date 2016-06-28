package org.nutz.plugins.webqq.model;

import java.util.ArrayList;
import java.util.List;

import org.nutz.json.Json;

/**
 * 群资料
 * 
 * @author ScienJus
 * @date 2015/12/24.
 */
public class GroupInfo {

	private long gid;

	private long createtime;

	private String memo;

	private String name;

	private long owner;

	private String markname;

	private List<GroupUser> users = new ArrayList<GroupUser>();

	public void addUser(GroupUser user) {
		this.users.add(user);
	}

	/**
	 * @return the gid
	 */
	public long getGid() {
		return gid;
	}

	/**
	 * @param gid
	 *            the gid to set
	 */
	public void setGid(long gid) {
		this.gid = gid;
	}

	/**
	 * @return the createtime
	 */
	public long getCreatetime() {
		return createtime;
	}

	/**
	 * @param createtime
	 *            the createtime to set
	 */
	public void setCreatetime(long createtime) {
		this.createtime = createtime;
	}

	/**
	 * @return the memo
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * @param memo
	 *            the memo to set
	 */
	public void setMemo(String memo) {
		this.memo = memo;
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
	 * @return the owner
	 */
	public long getOwner() {
		return owner;
	}

	/**
	 * @param owner
	 *            the owner to set
	 */
	public void setOwner(long owner) {
		this.owner = owner;
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
	 * @return the users
	 */
	public List<GroupUser> getUsers() {
		return users;
	}

	/**
	 * @param users
	 *            the users to set
	 */
	public void setUsers(List<GroupUser> users) {
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
