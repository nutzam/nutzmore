package org.nutz.integration.struts2;

import java.util.Map;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.impl.StrutsObjectFactory;
import org.apache.struts2.util.ObjectFactoryDestroyable;
import org.nutz.integration.Webs;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.Mvcs;

import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * 使用NutIoc来实现Struts2的ObjectFactory, 类似于Struts-Spring插件<p/>
 * <p/>
 * 用法:<p/> 
 * <ul>首先,建一个MainModule类,配上@IocBy</ul>
 * <ul>然后在你的web.xml中,添加</ul>
 * <pre>
 * {@code
 * <filter>
 *  <filter-name>nutz</filter-name>
 *  <filter-class>org.nutz.integration.NutServletContextMaster</filter-class>
 *  <init-param>
 *      <param-name>modules</param-name>
 *      <param-value>com.mine.app.MainModule</param-value>
 *  </init-param>
 * </filter>
 * }
 * </pre>
 * <ul>如果是struts.properties,则写上 </ul>
 * <pre>
 * {@code
 * struts.objectFactory = org.nutz.integration.struts2.NutObjectFactory
 * }
 * </pre>
 * <ul>如果是struts.xml,则写上</ul>
 * <pre>
 * {@code
 * <constant name="struts.objectFactory" value="org.nutz.integration.struts2.NutObjectFactory" />
 * }
 * </pre>
 * @author wendal(wendal1985@gmail.com)
 *
 */
@SuppressWarnings({"unchecked", "serial"})
public class NutObjectFactory extends StrutsObjectFactory implements ObjectFactoryDestroyable{
    
    private Ioc ioc;

    public Object buildAction(String actionName,
                              String namespace,
                              ActionConfig config,
                              Map<String, Object> extraContext) throws Exception {
        if (ioc().has(actionName))
            return ioc().get(getClassInstance(config.getClassName()),actionName);
        return ioc().get(getClassInstance(config.getClassName()));
    }

    public Object buildBean(@SuppressWarnings("rawtypes") Class clazz, Map<String, Object> extraContext) throws Exception {
        return ioc().get(clazz);
    }
    
    public Object buildBean(String className, Map<String, Object> extraContext) throws Exception {
        return ioc().get(getClassInstance(className));
    }
    
    public Object buildBean(String className,
                            Map<String, Object> extraContext,
                            boolean injectInternal) throws Exception {
        return ioc().get(getClassInstance(className));
    }
    
    public void destroy() {
        if (ioc != null)
            ioc.depose();
    }
    
    public Ioc ioc() {
        if (ioc != null)
            return ioc;
        Mvcs.setServletContext(ServletActionContext.getServletContext());
        ioc = Webs.ioc();
        return ioc;
    }
}
