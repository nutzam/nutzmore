package org.nutz.quartz;

/**
 * 让一个数组变成紧凑数组的时候，用来保留下标信息的对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T>
 */
public class QzObj<T> {

	public QzObj(T obj, int index) {
		this.obj = obj;
		this.index = index;
	}

	private T obj;

	private int index;

	public T getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String toString() {
		return index + ":" + (null == obj ? "<NULL>" : obj.toString());
	}
}
