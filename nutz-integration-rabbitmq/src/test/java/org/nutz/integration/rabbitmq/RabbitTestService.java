package org.nutz.integration.rabbitmq;

import static org.nutz.integration.rabbitmq.aop.RabbitmqMethodInterceptor.channel;

import java.io.IOException;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.IocBean;

import com.rabbitmq.client.AMQP.BasicProperties;

@IocBean
public class RabbitTestService {

    @Aop("rabbitmq")
    public void publish(String exchange, String routingKey, BasicProperties props, byte[] body) throws IOException {
        channel().basicPublish(exchange, routingKey, props, body);
    }
    
    @Aop("rabbitmq")
    public void publish(String routingKey, byte[] body) throws IOException {
        channel().basicPublish("", routingKey, null, body);
    }
}
