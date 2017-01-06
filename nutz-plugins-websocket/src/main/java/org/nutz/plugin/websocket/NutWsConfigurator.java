package org.nutz.plugin.websocket;

import javax.websocket.server.ServerEndpointConfig;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.Mvcs;

/**
 * 为WebSocket服务类提供Ioc支持
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
}

