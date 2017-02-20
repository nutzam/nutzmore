package org.nutz.rsf.test;
import net.hasor.core.AppContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
/**
 *
 */
public class RpcTest {
    private Ioc ioc;
    @Before
    public void before() throws ClassNotFoundException {
        ioc = new NutIoc(new ComboIocLoader("*js", "ioc/", "*hasor"));
    }
    @After
    public void after() {
        if (ioc != null) {
            ioc.depose();
        }
    }
    @Test
    public void testGetState() {
        AppContext appContext = ioc.get(AppContext.class);
        //
        //        0 = "org.nutz.integration.hasor.HasorAopConfigure"
        //        1 = "org.nutz.integration.hasor.HasorIocLoader"
        //        2 = "org.nutz.plugins.hasor.HasorIocLoader"
        //        3 = "org.nutz.plugins.hasor.HasorAopConfigure"
    }
}
