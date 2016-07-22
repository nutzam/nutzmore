package org.nutz.integration.dubbo.service;

import org.nutz.ioc.loader.annotation.IocBean;

import com.alibaba.dubbo.config.annotation.Reference;

@IocBean
public class CallService {

    @Reference(consumer="demoService")
    protected DemoService demoService;
    
    public String hi() {
        return demoService.hi("wendal");
    }
}
