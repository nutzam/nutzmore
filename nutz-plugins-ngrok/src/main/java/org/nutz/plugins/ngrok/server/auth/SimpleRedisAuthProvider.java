package org.nutz.plugins.ngrok.server.auth;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.nutz.integration.jedis.JedisAgent;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.plugins.ngrok.common.NgrokMsg;
import org.nutz.plugins.ngrok.server.NgrokServer;
import org.nutz.plugins.ngrok.server.NgrokServer.NgrokServerClient;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class SimpleRedisAuthProvider implements NgrokAuthProvider {
    
    public JedisAgent jedisAgent;
    
    public String key = "ngrok";
    
    public SimpleRedisAuthProvider() {}
    
    public SimpleRedisAuthProvider(String host, int port, String key) {
        jedisAgent = new JedisAgent(new JedisPool(host, port));
        this.key = key;
    }

    @Override
    public boolean check(NgrokServer srv, NgrokMsg auth) {
        String token = auth.getString("User");
        if (Strings.isBlank(token))
            return false;
        Jedis jedis = null;
        try {
            jedis = jedis();
            boolean re = "nil" != jedis.hget(key, token);
            if (re) {
                jedis.zadd("ngrok:lv", System.currentTimeMillis(), token);
                return true;
            }
            return token.equals(srv.redis_rkey);
        } catch (Throwable e){
            return false;
        } finally {
            Streams.safeClose(jedis);
        }
    }

    @Override
    public String[] mapping(NgrokServer srv, NgrokServerClient client, NgrokMsg req) {
        Jedis jedis = null;
        try {
            jedis = jedis();
            String[] tmp = null;
            String token = client.authMsg.getString("User");
            if (token.equals(srv.redis_rkey)) {
                tmp = new String[]{client.id.substring(0, 6) + "." + srv.srv_host};
            } else {
                String map = jedis.hget(key, token);
                if ("nil".equals(map))
                    return null;
                tmp = Strings.splitIgnoreBlank(map, ",");
                for (int i = 0; i < tmp.length; i++) {
                    if (!tmp[i].startsWith("#"))
                        tmp[i] += "." + srv.srv_host;
                }
            }
            return tmp;
        } catch (Throwable e){
            return null;
        } finally {
            Streams.safeClose(jedis);
        }
    }

    public Jedis jedis() {
        if (jedisAgent == null)
            jedisAgent = new JedisAgent(new JedisPool());
        return jedisAgent.getResource();
    }
    
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public void record(String host, long in, long out) {
        String rkey = key+":bytes:"+sdf.format(new Date());
        Jedis jedis = null;
        try {
            jedis = jedis();
            jedis.hincrBy(rkey, host, in+out);
        } catch (Exception e) {
        } finally {
            Streams.safeClose(jedis);
        }
    }
}
