package org.nutz.plugins.event;

import java.io.Serializable;

/**
 * 事件包裹对象
 * 消息通过此对象相互传递
 * 
 * @author qinerg@gmail.com
 * @varsion 2017-5-15
 */
public class Event implements Cloneable, Serializable {
	private static final long serialVersionUID = -6965416014564857939L;

	// 事件类型
	private String groupName;
	// 事件携带的对象
	private Object param;
	// 其它附加信息
	private String tag;
	// 处理完成回调
	private EventCallback callback;

	public Event() {
	}

	public Event(String groupName) {
		this(groupName, null);
	}

	public Event(String groupName, Object param) {
		this(groupName, param, null);
	}

	public Event(String groupName, Object param, String tag) {
		this(groupName, param, tag, null);
	}

	public Event(String groupName, Object param, String tag, EventCallback callback) {
		this.groupName = groupName;
		this.param = param;
		this.tag = tag;
		this.callback = callback;
	}

	public String getGroupName() {
		return groupName;
	}

	public Object getParam() {
		return param;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setParam(Object param) {
		this.param = param;
	}

	public EventCallback getCallback() {
		return callback;
	}

	public void setCallback(EventCallback callback) {
		this.callback = callback;
	}
}
