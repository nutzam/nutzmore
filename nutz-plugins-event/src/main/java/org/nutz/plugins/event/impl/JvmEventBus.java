package org.nutz.plugins.event.impl;

import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.event.Event;
import org.nutz.plugins.event.EventBus;
import org.nutz.plugins.event.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 单机事件总线
 *
 * @author qinerg@gmail.com
 * @varsion 2017-5-15
 */
@SuppressWarnings("unchecked")
public class JvmEventBus implements EventBus {

    private Log log = Logs.get();
    private Ioc ioc;
    private static Map<String, List<String>> eventMap = new ConcurrentHashMap<String, List<String>>();

    /**
     * 初始化，将容器中所有EventListener子类注册进来
     */
    public void init() {
        String[] listeners = ioc.getNamesByType(EventListener.class);
        for (String bean : listeners) {
            EventListener listener = ioc.get(EventListener.class, bean);
            String topic = listener.subscribeTopic();
            if (eventMap.containsKey(topic)) {
                eventMap.get(topic).add(bean);
            } else {
                List<String> list = new ArrayList<String>();
                list.add(bean);
                eventMap.put(topic, list);
            }
        }
    }

    public void depose() {
    }

    /**
     * 派发事件。事件处理器中的getGroupName方法的值与事件的groupName相同时，事件将被处理。
     *
     * @param event event
     */
    public <T extends Event> void fireEvent(T event) {
        if (event == null) {
            return;
        }
        if (eventMap.containsKey(event.getTopic())) {
            List<String> ql = eventMap.get(event.getTopic());
            // 循环派发事件
            for (String bean : ql) {
                try {
                    EventListener listener = ioc.get(EventListener.class, bean);
                    listener.onEvent(event);
                } catch (Exception e) {
                    log.error("event listener error!", e);
                }
            }
        }
    }

}