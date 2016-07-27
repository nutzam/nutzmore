package org.nutz.integration.dubbo.service;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;

import net.wendal.nutzbook.service.DubboWayService;

public class DubboClientTest {

    Ioc ioc;
    
    @Before
    public void before() throws ClassNotFoundException {
        // 载入配置
        ComboIocLoader loader = new ComboIocLoader("*anno", "org.nutz.integration.dubbo.service", "*dubbo", "dubbo-client.xml");
        ioc = new NutIoc(loader);
    }
    
    @After
    public void after() {
        if (ioc != null)
            ioc.depose();
    }

    @Test
    public void test_simple_hi() throws Exception {
        
        // 获取引用
        DubboWayService way = ioc.get(DubboWayService.class, "dubboWayService");
        
        // 执行调用
        String resp = way.hi("wendal");
        assertEquals(resp, "hi,wendal");
        
        String key = R.UU32();
        String value = R.UU32();
        way.redisSet(key, value);
        assertEquals(value, way.redisGet(key));
        Lang.quiteSleep(16*1000);
        assertNull(way.redisGet(key));
    }
}
