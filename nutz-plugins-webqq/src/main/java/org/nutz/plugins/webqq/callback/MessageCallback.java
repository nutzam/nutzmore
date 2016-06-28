package org.nutz.plugins.webqq.callback;

import org.nutz.plugins.webqq.model.DiscussMessage;
import org.nutz.plugins.webqq.model.GroupMessage;
import org.nutz.plugins.webqq.model.Message;

/**
 * 
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-webqq
 *
 * @file MessageCallback.java
 *
 * @description 消息回调
 *
 *
 * @DateTime 2016年6月28日 下午1:26:57
 *
 */
public interface MessageCallback {

	/**
	 * 收到私聊消息后的回调
	 * 
	 * @param message
	 */
	void onMessage(Message message);

	/**
	 * 收到群消息后的回调
	 * 
	 * @param message
	 */
	void onGroupMessage(GroupMessage message);

	/**
	 * 收到讨论组消息后的回调
	 * 
	 * @param message
	 */
	void onDiscussMessage(DiscussMessage message);
}
