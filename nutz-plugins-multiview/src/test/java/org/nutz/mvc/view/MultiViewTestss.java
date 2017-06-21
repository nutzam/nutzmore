package org.nutz.mvc.view;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.nutz.mock.Mock;
import org.nutz.mock.servlet.MockHttpServletRequest;
import org.nutz.mvc.Mvcs;
import org.nutz.plugins.view.AbstractTemplateViewResolver;

// extends BaseNutTest
public class MultiViewTestss{
    @Before
    public void before() {
        Mvcs.setServletContext(Mock.servlet.context());
    }
    @Test
    public void test_name() throws Exception {
        MockHttpServletRequest req = Mock.servlet.fullRequest();
      /* ResourceBundleViewResolver rbvr=ioc.get(ResourceBundleViewResolver.class);
       rbvr.make(ioc, "btl", "abc.bcd");*/
     AbstractTemplateViewResolver atvr=new org.nutz.plugins.view.JspView("abc.bcd");
        atvr.setPrefix("/WEB-INF");
        atvr.setSuffix(".jsp");
        atvr.setConfigPath("jsp");
       // atvr.setConfig(ioc.get(PropertiesProxy.class, "conf"));
        try {
			atvr.render(req, null, null);
		} catch (Throwable e) {
			e.printStackTrace();
		}
        assertEquals("/WEB-INF/abc/bcd.jsp", req.getDispatcherTarget());
        assertEquals(".do", req.getAttribute("servletExtension").toString());
        assertEquals("http://sta.denghuafeng.com/", req.getAttribute("resPath").toString());
       /* JspView fv = new JspView("abc.bcd");
        fv.render(req, null, null);
        assertEquals("/WEB-INF/abc/bcd.jsp", req.getDispatcherTarget());*/
    }
    @Test
    public void testMake() {
		// ResourceBundleViewResolver maker = new ResourceBundleViewResolver();
    	//ResourceBundleViewResolver maker = new ResourceBundleViewResolver();
    	//assertNotNull(maker.make(ioc, "btl", "auth.login"));
    	 //assertNotNull(maker.make(ioc, "ftl", "auth.login"));
    /*	ResourceBundleViewResolver maker = ioc.get(ResourceBundleViewResolver.class);
        assertNotNull(maker.make(null, "btl", "auth.login"));*/
      /*  assertNotNull(maker.make(null, "raw", "js"));
        assertNotNull(maker.make(null, "raw", "xml"));

        assertNotNull(maker.make(null, "jsp", "auth.login"));
        assertNotNull(maker.make(null, "jsp", "auth.login.jsp"));
        assertNotNull(maker.make(null, "json", "{}"));
        assertNotNull(maker.make(null, "json", "{compact:false,ignoreNull:false}"));
        assertNotNull(maker.make(null, "json", null));
        assertNotNull(maker.make(null, "void", "void"));
        assertNotNull(maker.make(null, "redirect", "/auth/login"));
        assertNotNull(maker.make(null, ">>", "/auth/login"));
        assertNotNull(maker.make(null, "forward", "/auth/login"));
        assertNotNull(maker.make(null, "->", "/auth/login"));
        
        assertNotNull(maker.make(null, "http", "404"));
        assertNotNull(maker.make(null, "http", "503"));*/
    }
}
