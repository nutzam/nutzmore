package org.nutz.plugins.mvc.websocket.handler;

import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.nutz.lang.Each;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

public class SimpleWsHandler extends AbstractWsHandler implements MessageHandler.Whole<String> {
    
    protected String nickname;

    public SimpleWsHandler() {
        this("wsroom:");
    }

    public SimpleWsHandler(String prefix) {
        super(prefix);
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
     * 设置昵称
     */
    public void nickname(NutMap msg) {
        String nickname = msg.getString("nickname");
        if (!Strings.isBlank(nickname))
            this.nickname = nickname;
    }
    
    /**
     * 发送消息给房间
     */
    public void msg2room(final NutMap msg) {
        final String room = msg.getString("room");
        if (room == null)
            return;
        endpoint.each(room, new Each<Session>() {
            public void invoke(int index, Session ele, int length) {
                if (ele.getId().equals(session.getId()))
                    return;
                NutMap resp = new NutMap("action", "msg");
                resp.setv("room", room);
                resp.setv("from", session.getId());
                resp.setv("msg", msg.get("data"));
                if (nickname != null)
                    resp.setv("nickname", nickname);
                endpoint.sendJson(ele.getId(), resp);
            }
        });
    }
}
