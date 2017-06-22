package org.nutz.plugins.mvc.websocket.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.nutz.Nutz;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.reflect.FastClassFactory;
import org.nutz.lang.reflect.FastMethod;
import org.nutz.lang.util.Callback;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.mvc.websocket.AbstractWsEndpoint;
import org.nutz.plugins.mvc.websocket.WsHandler;
import org.nutz.plugins.mvc.websocket.WsRoomProvider;

public abstract class AbstractWsHandler implements WsHandler {

    private static final Log log = Logs.get();

    protected Set<String> rooms;

    protected WsRoomProvider roomProvider;

    protected Session session;
    protected String prefix;
    protected HttpSession httpSession;
    protected AbstractWsEndpoint endpoint;

    protected Map<String, Callback<NutMap>> actions = new HashMap<>();

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


    public void init() {
        // 遍历所有public方法
        for (final Method method : getClass().getMethods()) {
            Class<?>[] paramTypes = method.getParameterTypes();
            // 如果仅一个参数,且参数类型NutMap的话, 就判定是WsAction方法咯
            if (paramTypes.length == 1 && NutMap.class.isAssignableFrom(paramTypes[0])) {
                // 如果有FastMethod实现的话...
                if (Nutz.majorVersion() == 1 && Nutz.minorVersion() > 60) {
                    final FastMethod fm = FastClassFactory.get(method);
                    actions.put(method.getName(), new Callback<NutMap>() {
                        public void invoke(NutMap msg) {
                            try {
                                fm.invoke(AbstractWsHandler.this, msg);
                            }
                            catch (Throwable e) {
                                onActionError(msg, e);
                            }
                        }
                    });
                } 
                // 老版本的话...
                else {
                    actions.put(method.getName(), new Callback<NutMap>() {
                        public void invoke(NutMap msg) {
                            try {
                                method.invoke(AbstractWsHandler.this, msg);
                            }
                            catch (Throwable e) {
                                onActionError(msg, e);
                            }
                        }
                    });
                }
            }
        }
    }


    /**
     * 处理消息, 将其转为NutMap,然后找对应的处理方法.
     */
    public void onMessage(String message) {
        try {
            NutMap msg = Json.fromJson(NutMap.class, message);
            String action = msg.getString("action");
            if (Strings.isBlank(action))
                return;
            Callback<NutMap> at = actions.get(action);
            if (at != null)
                at.invoke(msg);
            else
                defaultAction(msg);
        }
        catch (Throwable e) {
            onActionError(null, e);
        }
    }
    
    /**
     * 抛出异常的时候调用之
     */
    public void onActionError(NutMap msg, Throwable e) {
        if (log.isInfoEnabled())
            log.infof("bad message ? msg=%s",
                      Json.toJson(msg, JsonFormat.compact().setIgnoreNull(false)),
                      e);
    }

    /**
     * 没有任何action方法对应时,就调用它咯
     */
    public void defaultAction(NutMap msg) {
        if (log.isDebugEnabled())
            log.debugf("unknown action msg = %s",
                       Json.toJson(msg, JsonFormat.compact().setIgnoreNull(false)));
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

    public void setEndpoint(AbstractWsEndpoint endpoint) {
        this.endpoint = endpoint;
    }
}
