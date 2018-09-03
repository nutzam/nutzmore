package org.nutz.integration.cxf;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Vector;

import javax.jws.WebService;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.common.classloader.ClassLoaderUtils;
import org.apache.cxf.common.classloader.ClassLoaderUtils.ClassLoaderHolder;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.apache.cxf.transport.servlet.ServletController;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.resource.Scans;

public abstract class AbstractCxfModule extends CXFNonSpringServlet {

    private static final long serialVersionUID = 1L;

    private static final Log log = Logs.get();

    protected ServletController _controller;
    protected ClassLoader _loader;

    @Inject("refer:$ioc")
    protected Ioc ioc;

    protected String pathROOT = "/cxf/"; // 对应@At("/cxf")
    protected String pkg = "net.wendal.nutzbook.cxfdemo.webservice";

    @At("/*")
    public void service() throws Exception {
        final HttpServletRequest req = Mvcs.getReq();
        HttpServletResponse resp = Mvcs.getResp();
        // 下面代码, 源于doFilter方法.
        // 因为原方法中的loader和controller均为private属性,无法直接获取,所以需要中转一下
        ClassLoaderHolder origLoader = null;
        Bus origBus = null;
        final int trimLen = req.getContextPath().length() + pathROOT.length();
        try {
            if (_loader != null) {
                origLoader = ClassLoaderUtils.setThreadContextClassloader(_loader);
            }
            if (bus != null) {
                origBus = BusFactory.getAndSetThreadDefaultBus(bus);
            }
            if (_controller.filter(new HttpServletRequestWrapper(req) {
                @Override
                public String getServletPath() {
                    return pathROOT;
                }
                @Override
                public String getPathInfo() {
                    return req.getRequestURI().substring(trimLen);
                }
            }, resp)) {
                return;
            }
        }
        finally {
            if (origBus != bus) {
                BusFactory.setThreadDefaultBus(origBus);
            }
            if (origLoader != null) {
                origLoader.reset();
            }
        }
    }

    public void _init() throws Exception {
        // 模拟Servlet的初始化过程

        // 首先,建个map存初始化参数, 当然现在暂时是没东西咯
        final NutMap params = getInitParameters();
        // 初始化之
        init(new ServletConfig() {
            public String getServletName() {
                return "cxf";
            }

            public ServletContext getServletContext() {
                return Mvcs.getServletContext();
            }

            public Enumeration<String> getInitParameterNames() {
                return new Vector<String>(params.keySet()).elements();
            }

            public String getInitParameter(String name) {
                return params.getString(name);
            }
        });
        // 获取父类中私有的controller和loader属性
        Field _c = CXFNonSpringServlet.class.getDeclaredField("controller");
        _c.setAccessible(true);
        _controller = (ServletController) _c.get(this);
        _c = CXFNonSpringServlet.class.getDeclaredField("loader");
        _c.setAccessible(true);
        _loader = (ClassLoader) _c.get(this);
    }
    
    protected NutMap getInitParameters() {
        return new NutMap();
    }

    public void depose() {
        destroy(); // 销毁之
    }

    @Override
    protected void loadBus(ServletConfig sc) {
        super.loadBus(sc);
        Bus b = getBus();
        BusFactory.setDefaultBus(b);

        // 首先,拿到ioc容器
        for (Class<?> klass : Scans.me().scanPackage(pkg, null)) {
            // 有@WebService和@IocBean注解的非接口类
            WebService ws = klass.getAnnotation(WebService.class);
            if (ws == null || klass.isInterface())
                continue;
            if (Strings.isBlank(ws.serviceName())) {
                log.infof("%s has @WebService but serviceName is blank, ignore", klass.getName());
                continue;
            }
            log.debugf("add WebService addr=/%s type=%s", ws.serviceName(), klass.getName());
            //Endpoint.publish("/" + ws.serviceName(), ioc.get(klass));
            JaxWsServerFactoryBean sfb = new JaxWsServerFactoryBean();
            sfb.setServiceBean(ioc.get(klass));
            sfb.setBus(b);
            sfb.setAddress("/" + ws.serviceName());
            sfb.create();
            //System.out.println(sfb.getAddress());
        }

    }
}
