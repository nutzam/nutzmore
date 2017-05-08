package org.nutz.plugins.mvc.websocket.handler;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.mvc.websocket.WsHandler;
import org.nutz.plugins.mvc.websocket.WsRoomProvider;

public abstract class AbstractWsHandler implements WsHandler {

    private static final Log log = Logs.get();
    
    protected Set<String> rooms;
    
    protected WsRoomProvider roomProvider;
    
    protected Session session;
    protected String prefix;
    protected HttpSession httpSession;
    
    public AbstractWsHandler(String prefix) {
        rooms = new HashSet<>();
        this.prefix = prefix;
    }
    
    public void join(String room) {
        if (!Strings.isBlank(room)) {
            rooms.add(room);
            room = prefix + room;
            log.debugf("session(id=%s) join room(name=%s)", session.getId(), room);
            roomProvider.join(room, session.getId());
        }
    }
    
    public void left(String room) {
        if (!Strings.isBlank(room)) {
            rooms.remove(room);
            room = prefix + room;
            log.debugf("session(id=%s) left room(name=%s)", session.getId(), room);
            roomProvider.left(room, session.getId());
        }
    }

    public void depose() {
        for (String room : rooms) {
            left(room);
        }
    }

    public void setRoomProvider(WsRoomProvider roomProvider) {
        this.roomProvider = roomProvider;
    }
    
    public void setSession(Session session) {
        this.session = session;
    }
    
    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }
}
