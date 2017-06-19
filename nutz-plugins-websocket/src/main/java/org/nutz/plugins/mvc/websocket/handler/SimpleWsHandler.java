package org.nutz.plugins.mvc.websocket.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.MessageHandler;

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

public class SimpleWsHandler extends AbstractWsHandler implements MessageHandler.Whole<String> {

    private static final Log log = Logs.get();

    protected Map<String, Callback<NutMap>> actions = new HashMap<>();

    public SimpleWsHandler() {
        this("wsroom:");
    }

    public SimpleWsHandler(String prefix) {
        super(prefix);
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
                                fm.invoke(SimpleWsHandler.this, msg);
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
                                method.invoke(SimpleWsHandler.this, msg);
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
     * 抛出异常的时候调用之
     */
    public void onActionError(NutMap msg, Throwable e) {
        if (log.isInfoEnabled())
            log.infof("bad message ? msg=%s",
                      Json.toJson(msg, JsonFormat.compact().setIgnoreNull(false)),
                      e);
    }

    /**
     * 加入房间 对应的消息是  {action:"join", room:"wendal"}
     */
    public void join(NutMap msg) {
        join(msg.getString("room"));
    }

    /**
     * 退出房间 对应的消息是 {action:"left", room:"wendal"}
     */
    public void left(NutMap msg) {
        left(msg.getString("room"));
    }

    /**
     * 没有任何action方法对应时,就调用它咯
     */
    public void defaultAction(NutMap msg) {
        if (log.isDebugEnabled())
            log.debugf("unknown action msg = %s",
                       Json.toJson(msg, JsonFormat.compact().setIgnoreNull(false)));
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
}
