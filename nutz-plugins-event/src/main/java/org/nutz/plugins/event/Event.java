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

	// 事件主题
	private String topic;
	// 事件携带的对象
	private Object param;
	// 其它附加信息
	private String tag;
	// 处理完成回调
	private EventCallback callback = new EventCallback() {
		@Override
		public void onEventFinished(Object result) {
			// 默认不处理回调
		}
	};

	public Event() {
	}

	public Event(String topic) {
		this(topic, null);
	}

	public Event(String topic, Object param) {
		this(topic, param, null);
	}

	public Event(String topic, Object param, String tag) {
		this(topic, param, tag, null);
	}

	public Event(String topic, Object param, String tag, EventCallback callback) {
		this.topic = topic;
		this.param = param;
		this.tag = tag;
		this.callback = callback;
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
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
