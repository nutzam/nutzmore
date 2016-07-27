package org.nutz.integration.dubbo.service;

import org.nutz.ioc.loader.annotation.IocBean;

@IocBean(name="demoServiceLocal")
public class DemoServiceImpl implements DemoService {

    public DemoServiceImpl() {}
    
    public String hi(String name) {
        System.out.println("hi, everyone");
        return "hi,"+name;
    }
}
