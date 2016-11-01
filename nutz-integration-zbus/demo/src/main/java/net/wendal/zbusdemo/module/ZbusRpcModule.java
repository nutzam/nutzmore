package net.wendal.zbusdemo.module;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

import net.wendal.zbusdemo.service.UserRpcService;

@IocBean
@At("/zbus2")
@Filters
public class ZbusRpcModule {
    
    @Inject("refer:userRpcService")
    public UserRpcService userRpcService;

    @At("/send")
    @Ok("raw")
    @Fail("http:500")
    public String sayhi(@Param("name")String name) throws Exception {
        return userRpcService.sayhi(name);
    }

}
