package org.nutz.plugins.mvc.websocket;

import java.util.Set;

public interface WsRoomProvider {

    Set<String> wsids(String room);

    void join(String room, String wsid);

    void left(String room, String wsid);
}
