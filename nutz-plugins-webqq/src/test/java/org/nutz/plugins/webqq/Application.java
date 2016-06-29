package org.nutz.plugins.webqq;

import java.io.IOException;
import java.util.List;

import org.nutz.plugins.webqq.callback.MessageCallback;
import org.nutz.plugins.webqq.client.WebQQClient;
import org.nutz.plugins.webqq.model.Category;
import org.nutz.plugins.webqq.model.DiscussMessage;
import org.nutz.plugins.webqq.model.Friend;
import org.nutz.plugins.webqq.model.GroupMessage;
import org.nutz.plugins.webqq.model.Message;

/**
 * 
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-webqq
 *
 * @file Application.java
 *
 * @description 应用示例
 *
 *
 * @DateTime 2016年6月28日 下午1:26:35
 *
 */
public class Application {

	public static void main(String[] args) {
		// 创建一个新对象时需要扫描二维码登录，并且传一个处理接收到消息的回调，如果你不需要接收消息，可以传null
		WebQQClient client = new WebQQClient(new MessageCallback() {
			@Override
			public void onMessage(Message message) {
				System.out.println(message.getContent());
			}

			@Override
			public void onGroupMessage(GroupMessage message) {
				System.out.println(message.getContent());
			}

			@Override
			public void onDiscussMessage(DiscussMessage message) {
				System.out.println(message.getContent());
			}
		});
		// 登录成功后便可以编写你自己的业务逻辑了
		List<Category> categories = client.getFriendListWithCategory();
		for (Category category : categories) {
			System.out.println(category.getName());
			for (Friend friend : category.getFriends()) {
				System.out.println("————" + friend.getNickname());
			}
		}
		// 使用后调用close方法关闭，你也可以使用try-with-resource创建该对象并自动关闭
		try {
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
