package org.nutz.plugins.event.impl;

import org.nutz.plugins.event.Event;
import org.nutz.plugins.event.EventCallback;

/**
 * 默认实现事件对象
 *
 * @author zhengenshen@gmail.com
 * @date 2019/07/03 18:30
 */
public class SimpleEvent implements Event {


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

    public SimpleEvent() {
    }

    public SimpleEvent(String topic) {
        this(topic, null);
    }

    public SimpleEvent(String topic, Object param) {
        this(topic, param, null);
    }

    public SimpleEvent(String topic, Object param, String tag) {
        this(topic, param, tag, null);
    }

    public SimpleEvent(String topic, Object param, String tag, EventCallback callback) {
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
