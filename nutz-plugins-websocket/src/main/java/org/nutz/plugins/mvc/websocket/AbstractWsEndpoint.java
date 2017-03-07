package org.nutz.plugins.mvc.websocket;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.nutz.lang.random.R;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.mvc.websocket.room.MemoryRoomProvider;


public abstract class AbstractWsEndpoint extends Endpoint {

    protected ConcurrentHashMap<String, WsHandler> _handlers = new ConcurrentHashMap<>();
    protected ConcurrentHashMap<String, Session> _sessions = new ConcurrentHashMap<>();
    
    protected WsRoomProvider roomProvider = new MemoryRoomProvider();
    
    protected Field idField;
    
    protected static final Log log = Logs.get();
    
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        _sessions.remove(session.getId());
        WsHandler handler = _handlers.remove(session.getId());
        if (handler != null)
           handler.depose();
    }

    @OnError
    public void onError(Session session, java.lang.Throwable throwable) {
        onClose(session, null);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        changeSessionId(session);
        String wsid = session.getId();
        WsHandler handler = createHandler(session, config);
        handler.setRoomProvider(roomProvider);
        handler.setSession(session);
        session.addMessageHandler(handler);
        _sessions.put(wsid, session);
        _handlers.put(wsid, handler);
    }
    

    // WebSocketSession只对当前JVM是唯一的
    /** 所以我们要改造一下 */
    protected void changeSessionId(Session session) {
        try {
            if (idField == null) {
                idField = session.getClass().getDeclaredField("id");
                idField.setAccessible(true);
            }
            idField.set(session, R.UU32());
        }
        catch (Exception e) {
            log.debug("change session id fail. " + e.getMessage());
        }
    }

//    @OnMessage
//    public void onMessage(String message, Session session) {
//        WsHandler handler = _handlers.get(session.getId());
//        if (handler != null)
//            handler.onMessage(message);
//    }
    
    public abstract WsHandler createHandler(Session session, EndpointConfig config);
}
