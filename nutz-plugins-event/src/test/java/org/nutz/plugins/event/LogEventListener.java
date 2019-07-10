package org.nutz.plugins.event;

import org.nutz.aop.interceptor.async.Async;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.event.impl.SimpleEvent;

/**
 * @author gongqin@dhgate.com
 * @varsion 2017-5-15
 */
@IocBean
public class LogEventListener implements EventListener<SimpleEvent> {

    private Log log = Logs.get();

    @Override
    public String subscribeTopic() {
        return "log-event";
    }

    @Async
    @Override
    public void onEvent(SimpleEvent e) {
        log.debugf("->into log event: %s", e.getParam());
        Lang.sleep((Integer) e.getParam() * 1000);
        log.debugf("-> out log event: %s", e.getParam());
    }

}
