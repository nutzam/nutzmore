package org.nutz.integration.dubbo.service;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;


public class DubboIocLoaderTest {
    
    Ioc ioc;
    
    @Before
    public void before() throws ClassNotFoundException {
        // 载入配置
        ComboIocLoader loader = new ComboIocLoader("*anno", "org.nutz.integration.dubbo.service", "*dubbo", "simple-dubbo.xml");
        ioc = new NutIoc(loader);
    }
    
    @After
    public void after() {
        if (ioc != null)
            ioc.depose();
    }

    @Test
    public void test_simple_hi() throws Exception {
        // 导出服务
        ioc.get(null, "org.nutz.integration.dubbo.service.DemoService");
        
        // 获取引用
        DemoService demoService = ioc.get(DemoService.class, "demoServiceRemote");
        
        // 执行调用
        String resp = demoService.hi("wendal");
        
        assertEquals(resp, "hi,wendal");
    }
}
