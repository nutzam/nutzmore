package org.nutz.plugins.event;

/**
 * 事件完成后的回调(可选)
 * 主要用于异步事件完成后通知。请在事件监听器处理完成后主动调用
 * 
 * @author qinerg@gmail.com
 * @varsion 2017-5-15
 */
public interface EventCallback {

	/**
	 * 事件完成时回调
	 * @param result result
	 */
	void onEventFinished(Object result);

}
