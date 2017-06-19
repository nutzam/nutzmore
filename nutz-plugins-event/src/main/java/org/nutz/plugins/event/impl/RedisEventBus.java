package org.nutz.plugins.event.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.Ioc;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.event.Event;
import org.nutz.plugins.event.EventBus;
import org.nutz.plugins.event.EventListener;

/**
 * 基于redis的事件中心
 * 
 * @author qinerg@gmail.com
 * @varsion 2017-5-16
 */
public class RedisEventBus implements EventBus {
	private Log log = Logs.get();
	protected String prefix = "nutzevent-";
	protected long errorSleep = 10 * 1000;

	private Ioc ioc;
	private RedisService redisService;
	private ExecutorService executorService;

	/**
	 * 初始化，将容器中所有EventListener子类注册进来
	 */
	@Override
	public void init() {
	    if (executorService == null)
	        executorService = Executors.newCachedThreadPool();
		String[] listeners = ioc.getNamesByType(EventListener.class);
		for (final String bean : listeners) {
			EventListener listener = ioc.get(EventListener.class, bean);
			final String channelName = prefix + listener.subscribeTopic();

			executorService.submit(new Runnable() {
				public void run() {
					while (true) {
						String message = null;
						try {
							message = redisService.rpop(channelName);
							if (Strings.isBlank(message))
								continue;
							log.debugf("on %s -> %s", channelName, message);
						} catch (Exception e) {
							log.warnf("on %s error : %s", channelName, e.getMessage());
							// 当redis连接中断（报错）时，休眠x秒重新监听
							Lang.sleep(errorSleep);
							continue;
						}
						try {
							Event event = Json.fromJson(Event.class, message);
							EventListener listener = ioc.get(EventListener.class, bean);
							listener.onEvent(event);
						} catch (Exception e) {
							log.error("event listener error!", e);
							//redisService.lpush(channelName, message);  // 事件处理失败, 是否需要重新放回队列?
						}
					}
				}
			});
		}
	}
	
	@Override
	public void depose() {
	    if (executorService != null) {
	        executorService.shutdown();
	        try {
                executorService.awaitTermination(5, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
            }
	    }
	}

	/**
	 * 通过redis的list发布事件广播消息
	 */
	@Override
	public void fireEvent(Event event) {
		String channelName = prefix + event.getTopic(); //事件名
		String message = Json.toJson(event, JsonFormat.compact()); //事件体

		redisService.lpush(channelName, message);
		
	}

}
