package org.nutz.plugins.webqq.model;

import java.util.ArrayList;
import java.util.List;

import org.nutz.json.Json;

/**
 * 分组
 * 
 * @author ScienJus
 * @date 15/12/19.
 */
public class Category {

	private int index;

	private int sort;

	private String name;

	private List<Friend> friends = new ArrayList<Friend>();

	public void addFriend(Friend friend) {
		this.friends.add(friend);
	}

	@Override
	public String toString() {
		return Json.toJson(this);
	}

	public static Category defaultCategory() {
		Category category = new Category();
		category.setIndex(0);
		category.setSort(0);
		category.setName("我的好友");
		return category;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 *            the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the sort
	 */
	public int getSort() {
		return sort;
	}

	/**
	 * @param sort
	 *            the sort to set
	 */
	public void setSort(int sort) {
		this.sort = sort;
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
	 * @return the friends
	 */
	public List<Friend> getFriends() {
		return friends;
	}

	/**
	 * @param friends
	 *            the friends to set
	 */
	public void setFriends(List<Friend> friends) {
		this.friends = friends;
	}

}
