package org.nutz.plugins.mvc.websocket.handler;

import javax.websocket.MessageHandler;

import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

public class SimpleWsHandler extends AbstractWsHandler implements MessageHandler.Whole<String> {
    
    public SimpleWsHandler() {
        this("wsroom:");
    }

    public SimpleWsHandler(String prefix) {
        super(prefix);
    }

    public void onMessage(String message) {
        try {
            NutMap msg = Json.fromJson(NutMap.class, message);
            String action = msg.getString("action");
            if (Strings.isBlank(action))
                return;
            String room = msg.getString("room");
            switch (action) {
            case "join":
                join(room);
                break;
            case "left":
                left(room);
                break;
            default:
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
