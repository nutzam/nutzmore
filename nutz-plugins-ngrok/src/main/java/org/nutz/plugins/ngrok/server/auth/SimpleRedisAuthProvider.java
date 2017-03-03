package org.nutz.plugins.ngrok.server.auth;

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
        if (Strings.isBlank(auth.getString("User")))
            return false;
        Jedis jedis = null;
        try {
            jedis = jedis();
            return "nil" != jedis.hget(key, auth.getString("User"));
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
            String map = jedis.hget(key, client.authMsg.getString("User"));
            if ("nil".equals(map))
                return null;
            String[] tmp = Strings.splitIgnoreBlank(map, ",");
            for (int i = 0; i < tmp.length; i++) {
                if (!tmp[i].startsWith("#"))
                    tmp[i] += "." + srv.srv_host;
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
}
