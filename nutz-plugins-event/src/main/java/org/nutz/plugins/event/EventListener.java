package org.nutz.plugins.event;

/**
 * 事件监听接口，所有事件处理均需实现此接口
 * @author qinerg@gmail.com
 * @varsion 2017-5-15
 */
public interface EventListener {
	
	/**
	 * 处理的事件组名。仅组名相同的事件才会派发到此处理器
	 * @return 事件组名
	 */
	public String groupName();

	/**
	 * 事件触发
	 * @param e 事件消息体
	 */
	public void onEvent(Event e);

}
