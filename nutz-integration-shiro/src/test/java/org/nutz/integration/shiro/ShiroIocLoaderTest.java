package org.nutz.integration.shiro;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.integration.shiro.ioc.NutShiroEnvironmentLoader;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.mock.servlet.MockServletContext;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.impl.ServletValueProxyMaker;

public class ShiroIocLoaderTest {

    @Test
    public void testShiroIocLoader() throws ClassNotFoundException {
        MockServletContext sc = new MockServletContext();
        Mvcs.setServletContext(sc);
        NutIoc ioc = new NutIoc(new ComboIocLoader("*shiro"));
        ioc.addValueProxyMaker(new ServletValueProxyMaker(sc));
        assertNotNull(ioc.get(NutShiroEnvironmentLoader.class, "shiroEnv"));
        ioc.depose();
    }

}
