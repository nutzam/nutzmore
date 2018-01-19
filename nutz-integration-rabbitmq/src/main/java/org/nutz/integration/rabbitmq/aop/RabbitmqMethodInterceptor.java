package org.nutz.integration.rabbitmq.aop;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

@IocBean(name="rabbitmq")
public class RabbitmqMethodInterceptor implements MethodInterceptor {
    
    protected static ThreadLocal<Connection> _connection = new ThreadLocal<>();
    protected static ThreadLocal<Channel> _channel = new ThreadLocal<>();

    @Inject("refer:rabbitmq_cf")
    protected ConnectionFactory factory;
    
    @Override
    public void filter(InterceptorChain chain) throws Throwable {
        if (_connection.get() != null) {
            chain.doChain();
            return;
        }
        try {
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();
            _connection.set(conn);
            _channel.set(channel);
            chain.doChain();
        } finally {
            Streams.safeClose(_connection.get());
            _connection.remove();
            _channel.remove();
        }
    }

    public static Connection connection() {
        return _connection.get();
    }
    
    public static Channel channel() {
        return _channel.get();
    }
}
