package org.nutz.plugins.event;

/**
 * 事件中心接口
 *
 * @author qinerg@gmail.com
 * @varsion 2017-5-16
 * @see org.nutz.plugins.event.impl.JvmEventBus
 * @see org.nutz.plugins.event.impl.RedisEventBus
 */
public interface EventBus {
    /**
     * 初始化注册事件
     */
    void init();

    /**
     * 派发事件
     *
     * @param event event
     */
    <T extends Event> void fireEvent(T event);

    /**
     * 销毁操作
     */
    void depose();
}
