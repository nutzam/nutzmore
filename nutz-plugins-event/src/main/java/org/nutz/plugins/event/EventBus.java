package org.nutz.plugins.event;

/**
 * 事件中心接口
 * @see org.nutz.plugins.event.impl.JvmEventBus
 * @see org.nutz.plugins.event.impl.RedisEventBus
 * 
 * @author qinerg@gmail.com
 * @varsion 2017-5-16
 */
public interface EventBus {
	/**
	 * 初始化注册事件
	 */
	void init();

	/**
	 * 派发事件
	 * @param event
	 */
	void fireEvent(Event event);

	/**
	 * 销毁操作
	 */
	void depose();
}
