package org.nutz.plugins.mvc.websocket;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.Mvcs;

/**
 * 为WebSocket服务类提供Ioc支持
 * 
 * @author wendal
 *
 */
public class NutWsConfigurator extends ServerEndpointConfig.Configurator {

    public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
        Ioc ioc = Mvcs.getIoc();
        if (ioc == null)
            ioc = Mvcs.ctx().getDefaultIoc();
        return ioc.get(endpointClass);
    }

    public void modifyHandshake(ServerEndpointConfig sec,
                                HandshakeRequest request,
                                HandshakeResponse response) {
        // 如果是Nutz MVC环境, 从mvc上下文直接获取Request对象
        HttpServletRequest req = Mvcs.getReq();
        if (req != null) {
            sec.getUserProperties().put("HttpSession", req.getSession(false));
            return;
        }
        javax.servlet.http.HttpSession session = (javax.servlet.http.HttpSession) request.getHttpSession();
        if (session != null)
            sec.getUserProperties().put("HttpSession", session);
    }
}
