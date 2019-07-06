package org.nutz.plugins.event;

import java.io.Serializable;

/**
 * 事件接口
 *
 * @author qinerg@gmail.com
 * @version 2017-5-15
 */
public interface Event extends Cloneable, Serializable {

    /**
     * 事件主题
     *
     * @return topic
     */
    String getTopic();

    /**
     * 回调方法
     *
     * @return callback
     */
    EventCallback getCallback();
}
