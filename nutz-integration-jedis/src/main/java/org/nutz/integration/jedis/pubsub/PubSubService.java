package org.nutz.integration.jedis.pubsub;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.ArrayList;
import java.util.List;

import org.nutz.ioc.aop.Aop;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.JedisPool;

public class PubSubService {
    
    private static final Log log = Logs.get();
    
    protected JedisPool jedisPool;
    
    List<PubSubProxy> list = new ArrayList<>();

    public void reg(final String pattern, PubSub pb) {
        final PubSubProxy proxy = new PubSubProxy(pattern, pb);
        list.add(proxy);
        new Thread("jedis.pubsub." + pattern) {
            public void run() {
                jedisPool.getResource().psubscribe(proxy, pattern);
            }
        }.start();
    }
    
    @Aop("redis")
    public void fire(String channel, String message) {
        log.debugf("publish channel=%s msg=%s", channel, message);
        jedis().publish(channel, message);
    }

    public void depose() {
        for (PubSubProxy proxy : list)
            proxy.punsubscribe(proxy.pattern);
    }
}
