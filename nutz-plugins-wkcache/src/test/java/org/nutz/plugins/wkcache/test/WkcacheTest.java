package org.nutz.plugins.wkcache.test;

import org.junit.Assert;
import org.junit.Test;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * Created by wizzer on 2017/6/14.
 */

public class WkcacheTest extends Assert {

    private static final Log log = Logs.get();

    @Test
    public void testIocLoader() throws ClassNotFoundException {
        NutIoc ioc = new NutIoc(new ComboIocLoader("*anno", "org.nutz.plugins.wkcache", "*jedis", "*wkcache"));
        PropertiesProxy conf = new PropertiesProxy();
        conf.put("wkcache.nutzwk_cache", "1800");
        conf.put("wkcache.cache_time_1", "18000");
        conf.put("wkcache.cache_time_2", "28000");
        ioc.getIocContext().save("app", "conf", new ObjectProxy(conf));
        assertTrue(ioc.getNames().length > 0);
        log.debug(ioc.get(MyCacheTest.class).testCache("wizzer.cn"));
//        ioc.get(MyCacheTest.class).testRemove();
//        log.debug(ioc.get(MyCacheTest.class).testCache("大鲨鱼最帅"));
//        ioc.get(MyCacheTest.class).testRemoveAll();
//        log.debug(ioc.get(MyCacheTest.class).testCacheEl("el"));
        TestBean test = new TestBean();
        test.setId("abc");
        test.setName("def");
        for (int i = 0; i < 1000; i++)
            log.debug(ioc.get(MyCacheTest.class).testCacheObj(test));
//        ioc.get(MyCacheTest.class).testRemove(test);
        ioc.get(MyCacheTest.class).cache_time_1("hello1");
        ioc.get(MyCacheTest.class).cache_time_2("hello2");
    }


}