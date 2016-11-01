package net.wendal.zbusdemo.module;

import org.nutz.integration.zbus.ZBusProducer;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.zbus.net.http.Message;

@IocBean
@At("/zbus")
@Filters
public class ZbusModule {
    
    @Inject("java:$zbus.getProducer('user')")
    public ZBusProducer topicUpdateMq;

    @At("/send")
    @Ok("raw")
    @Fail("http:500")
    public Message sendMessage(@Param("msg")String msg, @Param("async")boolean async) throws Exception {
        Message resp = null;
        if (async)
            resp = topicUpdateMq.sendSync(new Message(msg));
        else
            resp = topicUpdateMq.sendSync(new Message(msg));
        return resp;
    }

}
