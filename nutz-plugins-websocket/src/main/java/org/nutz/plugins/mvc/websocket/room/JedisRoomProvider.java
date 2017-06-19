package org.nutz.plugins.mvc.websocket.room;

import java.util.Set;

import org.nutz.integration.jedis.JedisAgent;
import org.nutz.plugins.mvc.websocket.WsRoomProvider;

import redis.clients.jedis.Jedis;

/**
 * 基于Redis/Jedis的Websocket房间实现
 * 
 * @author wendal
 *
 */
public class JedisRoomProvider implements WsRoomProvider {

    protected JedisAgent jedisAgent;

    public JedisRoomProvider(JedisAgent jedisAgent) {
        this.jedisAgent = jedisAgent;
    }

    public Set<String> wsids(String room) {
        try (Jedis jedis = jedisAgent.getResource()) {
            return jedis.smembers(room);
        }
    }

    public void join(String room, String wsid) {
        try (Jedis jedis = jedisAgent.getResource()) {
            jedis.sadd(room, wsid);
        }
    }

    public void left(String room, String wsid) {
        try (Jedis jedis = jedisAgent.getResource()) {
            jedis.srem(room, wsid);
        }
    }

}
