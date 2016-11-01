package net.wendal.zbusdemo.service;

import java.io.IOException;

import org.nutz.dao.Dao;
import org.nutz.integration.zbus.annotation.ZBusConsumer;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.zbus.mq.Consumer;
import org.zbus.mq.Consumer.ConsumerHandler;
import org.zbus.net.http.Message;

@IocBean
@ZBusConsumer(mq="user") // 声明自身为消费者
public class UserService implements ConsumerHandler {
    
    private static Log log = Logs.get();

    @Inject 
    protected Dao dao;

    // 处理mq输入
    public void handle(Message msg, Consumer consumer) throws IOException {
        log.warn("get msg = " + msg);
    }

}
