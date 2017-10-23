package org.nutz.integration.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RabbitmqIocLoaderTest {

    private Ioc ioc;

    @Before
    public void before() throws ClassNotFoundException {
        ioc = new NutIoc(new ComboIocLoader("*js", "ioc/", "*anno", "org.nutz.integration.rabbitmq", "*rabbitmq"));
    }

    @After
    public void after() {
        if (ioc != null)
            ioc.depose();
    }

    @Test
    public void testRabbitmqIocLoader() throws IOException, TimeoutException {
        final String QUEUE_NAME = "hello";
        ConnectionFactory cf = ioc.get(ConnectionFactory.class, "rabbitmq_cf");
        try (Connection conn = cf.newConnection();) {
            assertNotNull(conn);
        }

        RabbitTestService ts = ioc.get(RabbitTestService.class);

        // 开一个发送者, 创建queue.
        Connection sender_conn = cf.newConnection();
        Channel sender_channel = sender_conn.createChannel();
        sender_channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        sender_conn.close();

        // 开一个接收者
        final String[] msg = new String[1];
        Connection consumer_conn = cf.newConnection();
        Channel consumer_channel = consumer_conn.createChannel();
        consumer_channel.basicConsume(QUEUE_NAME, new DefaultConsumer(consumer_channel) {
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       BasicProperties properties,
                                       byte[] body)
                    throws IOException {

                msg[0] = new String(body);
            }
        });

        ts.publish(QUEUE_NAME, "wendal".getBytes());

        Lang.quiteSleep(1000);
        consumer_channel.close();
        assertEquals("wendal", msg[0]);
    }

}
