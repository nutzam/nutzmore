package org.nutz.integration.dubbo.service;

import com.alibaba.dubbo.config.annotation.Service;

@Service(version="1.0.0")
public interface DemoService {
    
    String hi(String name);
}
