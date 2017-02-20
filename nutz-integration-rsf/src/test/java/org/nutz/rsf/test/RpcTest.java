package org.nutz.rsf.test;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.rsf.test.provider.EchoService;
/**
 *
 */
public class RpcTest {
    private Ioc ioc;
    @Before
    public void before() throws ClassNotFoundException {
        ioc = new NutIoc(new ComboIocLoader("*hasor", "rsf.properties"));
    }
    @After
    public void after() {
        if (ioc != null) {
            ioc.depose();
        }
    }
    @Test
    public void testGetState() throws InterruptedException {
        EchoService echoService = ioc.get(EchoService.class);
        String sayHello = echoService.sayHello("hello word");
        System.out.println(sayHello);
        //
    }
}