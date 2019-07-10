package org.nutz.plugins.event.impl;

import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.Ioc;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.event.Event;
import org.nutz.plugins.event.EventBus;
import org.nutz.plugins.event.EventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 基于redis的事件中心
 *
 * @author qinerg@gmail.com
 * @varsion 2017-5-16
 */
@SuppressWarnings("unchecked")
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
                        byte[] message;
                        try {
                            message = redisService.rpop(channelName.getBytes());
                            if (Lang.isEmpty(message))
                                continue;
                        } catch (Exception e) {
                            log.warnf("on %s error : %s", channelName, e.getMessage());
                            // 当redis连接中断（报错）时，休眠x秒重新监听
                            Lang.sleep(errorSleep);
                            continue;
                        }
                        try {
                            Event event = (Event) to(message);
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
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * 通过redis的list发布事件广播消息
     */
    @Override
    public <T extends Event> void fireEvent(T event) {
        if (event == null) {
            return;
        }
        String channelName = prefix + event.getTopic(); //事件名


        String message = Json.toJson(event, JsonFormat.compact()); //事件体

        redisService.lpush(channelName.getBytes(), to(event));

    }

    private byte[] to(Object obj) {
        ObjectOutputStream os = null;
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bs);
            os.writeUnshared(obj);
            bytes = bs.toByteArray();
        } catch (Exception e) {
            log.info("object to bytes fail", e);
        } finally {
            Streams.safeClose(os);
        }
        return bytes;
    }


    private Object to(byte[] bytes) {
        ObjectInputStream os = null;
        Object obj = null;
        try {
            os = new ObjectInputStream(new ByteArrayInputStream(bytes));
            obj = os.readObject();
        } catch (Exception e) {
            log.info(" bytes to Object fail", e);
        } finally {
            Streams.safeClose(os);
        }
        return obj;
    }

}
