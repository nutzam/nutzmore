package org.nutz.integration.dubbo.service;

import com.alibaba.dubbo.config.annotation.Service;

@Service(version="1.0.0")
public class DemoServiceImpl implements DemoService {

    public DemoServiceImpl() {}
    
    public String hi(String name) {
        return "hi,"+name;
    }
}
