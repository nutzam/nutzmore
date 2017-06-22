package org.nutz.plugins.mvc.websocket;

import javax.servlet.http.HttpSession;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

public interface WsHandler extends MessageHandler {
    
    /**
     * 在所有setXXX方法执行完毕后才会调用的初始化方法
     */
    void init();

    /**
     * 设置WebSocket Session实例
     * @param session WebSocket会话实例,肯定不是null
     */
    void setSession(Session session);

    /**
     * 设置关联的HttpSession
     * @param httpSession 设置所关联的会话, 可能是null
     */
    void setHttpSession(HttpSession httpSession);

    /**
     * 设置房间的实现类的实例
     */
    void setRoomProvider(WsRoomProvider roomProvider);

    /**
     * Endpoint实现类的实例,主要是为了提高websocket发送消息的api
     */
    void setEndpoint(AbstractWsEndpoint endpoint);

    /**
     * 处理页面端发送过来的文本信息
     */
    void onMessage(String msg);

    /**
     * 会话关闭时的回调
     */
    void depose();
}
