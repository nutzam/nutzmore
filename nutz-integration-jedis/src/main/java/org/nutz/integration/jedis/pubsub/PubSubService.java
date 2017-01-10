package org.nutz.integration.jedis.pubsub;

import java.util.ArrayList;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import redis.clients.jedis.Jedis;
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
                int count = 1;
                while (!jedisPool.isClosed()) {
                    try {
                        log.debug("psubscribe " + pattern);
                        jedisPool.getResource().psubscribe(proxy, pattern);
                    }
                    catch (Exception e) {
                        if (jedisPool.isClosed())
                            break;
                        log.debug("psubscribe fail, retry after "+count+"seconds", e);
                        Lang.quiteSleep(count * 1000);
                        if (count < 15)
                            count ++;
                    }
                }
            }
        }.start();
    }
    
    public void fire(String channel, String message) {
        log.debugf("publish channel=%s msg=%s", channel, message);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.publish(channel, message);
        } finally {
            Streams.safeClose(jedis);
        }
    }

    public void depose() {
        for (PubSubProxy proxy : list)
            try {
                proxy.punsubscribe(proxy.pattern);
            }
            catch (Exception e) {
                log.debug("punsubscribe " + proxy.pattern, e);
            }
    }
}
