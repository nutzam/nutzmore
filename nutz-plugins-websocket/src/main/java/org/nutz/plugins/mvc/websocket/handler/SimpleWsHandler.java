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
    public void join(NutMap req) {
        join(req.getString("room"));
    }

    /**
     * 退出房间 对应的消息是 {action:"left", room:"wendal"}
     */
    public void left(NutMap req) {
        left(req.getString("room"));
    }
    
    /**
     * 设置昵称
     */
    public void nickname(NutMap req) {
        String nickname = req.getString("nickname");
        if (!Strings.isBlank(nickname))
            this.nickname = nickname;
    }
    
    /**
     * 发送消息给房间
     */
    public void msg2room(final NutMap req) {
        final String room = req.getString("room");
        if (room == null)
            return;
        String _room = room;
        if (prefix.length() > 0 && !room.startsWith(prefix))
            _room = prefix + room;
        endpoint.each(_room, new Each<Session>() {
            public void invoke(int index, Session ele, int length) {
                if (ele.getId().equals(session.getId()))
                    return;
                NutMap resp = new NutMap("action", "msg");
                resp.setv("room", room);
                resp.setv("from", session.getId());
                resp.setv("msg", req.get("msg"));
                if (nickname != null)
                    resp.setv("nickname", nickname);
                endpoint.sendJson(ele.getId(), resp);
            }
        });
    }
}
