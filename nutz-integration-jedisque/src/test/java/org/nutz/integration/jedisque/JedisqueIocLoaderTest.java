package org.nutz.integration.jedisque;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.ObjectProxy;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.combo.ComboIocLoader;

import com.github.xetorthio.jedisque.Jedisque;

public class JedisqueIocLoaderTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws ClassNotFoundException {
        Ioc ioc = new NutIoc(new ComboIocLoader("*jedisque"));
        PropertiesProxy conf = new PropertiesProxy();
        conf.set("disque.uris", "disque://120.24.240.16:7711");
        ((NutIoc) ioc).getIocContext().save("app", "conf", new ObjectProxy(conf));
        try (Jedisque jedisque = ioc.get(Jedisque.class)) {
            jedisque.ping();
        }
        try (Jedisque jedisque = ioc.get(Jedisque.class)) {
            jedisque.ping();
        }
        ioc.depose();
    }
}
