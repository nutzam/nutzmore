package org.nutz.plugins.mvc.websocket.room;

import static org.nutz.integration.jedis.RedisInterceptor.jedis;

import java.util.Set;

import org.nutz.ioc.aop.Aop;
import org.nutz.plugins.mvc.websocket.WsRoomProvider;

public class JedisRoomProvider implements WsRoomProvider {

    @Aop("redis")
    public Set<String> wsids(String room) {
        return jedis().smembers(room);
    }

    @Aop("redis")
    public void join(String room, String wsid) {
        jedis().sadd(room, wsid);
    }

    @Aop("redis")
    public void left(String room, String wsid) {
        jedis().srem(room, wsid);
    }
    
    
}
