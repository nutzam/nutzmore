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
        //ioc.get(MyCacheTest.class).cache_time_2("hello2");
    }


    @Test
    public void testCacheNull() throws ClassNotFoundException {
        NutIoc ioc = new NutIoc(new ComboIocLoader("*anno", "org.nutz.plugins.wkcache", "*jedis", "*wkcache"));

        ioc.getIocContext().save("app", "conf", new ObjectProxy(new PropertiesProxy()));
        assertTrue(ioc.getNames().length > 0);
        MyCacheTest myCacheTest = ioc.get(MyCacheTest.class);
        myCacheTest.testCacheNull(true);
        assertNull(myCacheTest.testCacheNull(false));

        myCacheTest.testCacheIgnoreNull(true);
        assertNotNull(myCacheTest.testCacheIgnoreNull(false));
    }

    @Test
    public void testRemoveAllWithScan() throws ClassNotFoundException {
        NutIoc ioc = new NutIoc(new ComboIocLoader("*anno", "org.nutz.plugins.wkcache", "*jedis", "*wkcache"));

        ioc.getIocContext().save("app", "conf", new ObjectProxy(new PropertiesProxy()));
        assertTrue(ioc.getNames().length > 0);
        MyCacheTest myCacheTest = ioc.get(MyCacheTest.class);
        int flag = 0;
        myCacheTest.testRemoveAll();
        myCacheTest.testCacheFlag("s_1", flag);
        myCacheTest.testCacheFlag("s_2", flag);

        Assert.assertEquals("s_1_" + flag, myCacheTest.testCacheFlag("s_1", flag + 1));
        Assert.assertEquals("s_2_" + flag, myCacheTest.testCacheFlag("s_2", flag + 1));
        myCacheTest.testRemoveAll();
        // 删除缓存后，flag 值就变成了传入的新值
        Assert.assertEquals("s_1_" + (flag + 1), myCacheTest.testCacheFlag("s_1", flag + 1));
        Assert.assertEquals("s_2_" + (flag + 1), myCacheTest.testCacheFlag("s_2", flag + 1));
        // 清除缓存
       // myCacheTest.testRemoveAll();
    }

    @Test
    public void testRemoveMulKey() throws ClassNotFoundException {
        NutIoc ioc = new NutIoc(new ComboIocLoader("*anno", "org.nutz.plugins.wkcache", "*jedis", "*wkcache"));

        ioc.getIocContext().save("app", "conf", new ObjectProxy(new PropertiesProxy()));
        assertTrue(ioc.getNames().length > 0);
        MyCacheTest myCacheTest = ioc.get(MyCacheTest.class);
        myCacheTest.testRemoveAll();
        int flag = 1;
        String first1 = myCacheTest.testCacheFlag("key_1", flag);
        String second1 = myCacheTest.testCacheFlag("key_1", flag + 1);
        Assert.assertEquals(first1, second1);

        String first2 = myCacheTest.testCacheFlag("key_2", flag);
        String second2 = myCacheTest.testCacheFlag("key_2", flag + 1);
        Assert.assertEquals(first2, second2);


        String first3 = myCacheTest.testCacheFlag("kk_1", flag);
        String second3 = myCacheTest.testCacheFlag("kk_1", flag + 1);
        Assert.assertEquals(first3, second3);

        String first4 = myCacheTest.testCacheFlag("kk_2", flag);
        String second4 = myCacheTest.testCacheFlag("kk_2", flag + 1);
        Assert.assertEquals(first4, second4);
        // 删除缓存
        myCacheTest.testRemoveMulKey("key_1,key_2,kk_*");
        // 删除缓存后，flag 值就变成了传入的新值
        Assert.assertEquals("key_1_" + (flag + 1), myCacheTest.testCacheFlag("key_1", flag + 1));
        Assert.assertEquals("key_2_" + (flag + 1), myCacheTest.testCacheFlag("key_2", flag + 1));
        Assert.assertEquals("kk_1_" + (flag + 1), myCacheTest.testCacheFlag("kk_1", flag + 1));
        Assert.assertEquals("kk_2_" + (flag + 1), myCacheTest.testCacheFlag("kk_2", flag + 1));

        // 删除缓存
        myCacheTest.testRemoveMulKey("key_1,key_2,kk_*");
    }

    @Test
    public void testRemoveWithScan() throws ClassNotFoundException {
        NutIoc ioc = new NutIoc(new ComboIocLoader("*anno", "org.nutz.plugins.wkcache", "*jedis", "*wkcache"));

        ioc.getIocContext().save("app", "conf", new ObjectProxy(new PropertiesProxy()));
        assertTrue(ioc.getNames().length > 0);
        MyCacheTest myCacheTest = ioc.get(MyCacheTest.class);
        myCacheTest.testRemoveAll();
    }
}