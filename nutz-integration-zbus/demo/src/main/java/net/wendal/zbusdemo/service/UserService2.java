package net.wendal.zbusdemo.service;

import org.nutz.integration.zbus.annotation.ZBusService;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
@ZBusService // 演示RPC服务的提供者
public class UserService2 implements UserRpcService {

    public String sayhi(String name) {
        if ("god".equals(name))
            return "OMG";
        return "hi, "+ name;
    }

}
