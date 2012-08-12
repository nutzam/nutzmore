package org.nutz.integration.struts2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.impl.StrutsObjectFactory;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.Ioc2;
import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.config.AbstractNutConfig;
import org.nutz.mvc.impl.ServletValueProxyMaker;

import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.inject.Inject;

/**
 * 使用NutIoc来实现Struts2的ObjectFactory, 类似于Struts-Spring插件<p/>
 * <p/>
 * 用法:<p/> 
 * <ul>首先,建一个MainModule类,配上@IocBy</ul>
 * <ul>如果是struts.properties,则写上 </ul>
 * <pre>
 * {@code
 * struts.objectFactory = org.nutz.integration.struts2.NutObjectFactory
 * struts.objectFactory.mainModule = xxx.xxx.xx.MainModule
 * }
 * </pre>
 * <ul>如果是struts.xml,则写上</ul>
 * <pre>
 * {@code
 * <constant name="struts.objectFactory" value="org.nutz.integration.struts2.NutObjectFactory" />
 * <constant name="struts.objectFactory.mainModule" value="xxx.xxx.xx.MainModule" />
 * }
 * </pre>
 * @author wendal(wendal1985@gmail.com)
 *
 */
@SuppressWarnings({"unchecked", "serial"})
public class NutObjectFactory extends StrutsObjectFactory implements ObjectFactoryDestroyable{
    
    private static final Log log = Logs.get();
    
    private Ioc ioc;
    
    public NutObjectFactory(@Inject(value="struts.objectFactory.mainModule",required=true)String mainModule) throws ClassNotFoundException {
        IocBy ib = Class.forName(mainModule).getAnnotation(IocBy.class);
        if (ib == null)
            throw new IllegalArgumentException("Need IocBy!!");
        if (log.isDebugEnabled())
            log.debugf("@IocBy(%s)", ib.type().getName());

        final ServletContext context = ServletActionContext.getServletContext();
        NutConfig config = new AbstractNutConfig(context) {
            
            public ServletContext getServletContext() {
                return context;
            }
            
            public List<String> getInitParameterNames() {
                return new ArrayList<String>(0);
            }
            
            public String getInitParameter(String name) {
                return null;
            }
            
            public String getAppName() {
                return "struts-nutz";
            }
        };
        ioc = Mirror.me(ib.type()).born().create(config, ib.args());
        // 如果是 Ioc2 的实现，增加新的 ValueMaker
        if (ioc instanceof Ioc2) {
            ((Ioc2) ioc).addValueProxyMaker(new ServletValueProxyMaker(config.getServletContext()));
        }
    }

    public Object buildAction(String actionName,
                              String namespace,
                              ActionConfig config,
                              Map<String, Object> extraContext) throws Exception {
        if (ioc.has(actionName))
            return ioc.get(getClassInstance(config.getClassName()),actionName);
        return ioc.get(getClassInstance(config.getClassName()));
    }

    public Object buildBean(@SuppressWarnings("rawtypes") Class clazz, Map<String, Object> extraContext) throws Exception {
        return ioc.get(clazz);
    }
    
    public Object buildBean(String className, Map<String, Object> extraContext) throws Exception {
        return ioc.get(getClassInstance(className));
    }
    
    public Object buildBean(String className,
                            Map<String, Object> extraContext,
                            boolean injectInternal) throws Exception {
        return ioc.get(getClassInstance(className));
    }
    
    public void destroy() {
        if (ioc != null)
            ioc.depose();
    }
}
