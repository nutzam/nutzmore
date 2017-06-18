package org.nutz.plugins.mvc.websocket.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.MessageHandler;

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
        for (Method method : getClass().getMethods()) {
            Class<?>[] paramTypes = method.getParameterTypes();
            if (paramTypes.length == 1 && NutMap.class.isAssignableFrom(paramTypes[0])) {
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
        }
    }

    public void onActionError(NutMap msg, Throwable e) {
        if (log.isInfoEnabled())
            log.infof("bad message ? msg=%s",
                      Json.toJson(msg, JsonFormat.compact().setIgnoreNull(false)),
                      e);
    }

    public void join(NutMap msg) {
        join(msg.getString("room"));
    }

    public void left(NutMap msg) {
        left(msg.getString("room"));
    }

    public void defaultAction(NutMap msg) {
        if (log.isDebugEnabled())
            log.debugf("unknown action msg = %s",
                       Json.toJson(msg, JsonFormat.compact().setIgnoreNull(false)));
    }

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
