package org.nutz.quartz;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.Lang;

/**
 * 封装了叠加数组的元素
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class QzOverlapor {

	private List<Object> list;

	public QzOverlapor() {
		list = new LinkedList<Object>();
	}

	/**
	 * @return 叠加列表的长度
	 */
	public int size() {
		return list.size();
	}

	/**
	 * @return 叠加列表的是否为空
	 */
	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * 增加一个对象到列表中
	 * 
	 * @param obj
	 *            对象
	 */
	public void add(Object obj) {
		list.add(obj);
	}

	/**
	 * 将内部列表，变成一个方便使用的数组
	 * 
	 * @param <T>
	 * @param eleType
	 *            数组元素类型
	 * @return 数组
	 */
	@SuppressWarnings("unchecked")
	public <T> T[] toArray(Class<T> eleType) {
		if (null == list || list.isEmpty())
			return (T[]) Array.newInstance(eleType, 0);

		Object array = Array.newInstance(eleType, list.size());
		int i = 0;
		for (Object obj : list) {
			Array.set(array, i++, obj);
		}
		return (T[]) array;
	}

	public String toString() {
		return isEmpty() ? "" : Lang.concat(',', list).toString();
	}

}
