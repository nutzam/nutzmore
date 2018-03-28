package org.nutz.integration.rabbitmq;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import com.rabbitmq.client.ConnectionFactory;

@IocBean(create = "init", depose = "depose")
public class RabbitMqBeans {

    @Inject
    public PropertiesProxy conf;

    protected ExecutorService es;

    public void init() {
        es = Executors.newFixedThreadPool(conf.getInt("rabbitmq.concurrency", 10));
    }

    public void depose() {
        if (es != null)
            es.shutdown();
    }

    @IocBean(name = "rabbitmq_cf")
    public ConnectionFactory createConnectionFactory() {
        ConnectionFactory connectionFactory = conf.make(ConnectionFactory.class, "rabbitmq.");
        connectionFactory.setSharedExecutor(es);
        return connectionFactory;
    }

}
