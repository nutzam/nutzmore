package org.nutz.integration.spring.xml;

import java.lang.annotation.Annotation;

import org.nutz.aop.ClassAgent;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class NutIocSpringBridgeBeanDefinitionParser implements BeanDefinitionParser {
    
    private static final Log log = Logs.get();

    @SuppressWarnings("deprecation")
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Ioc ioc = Mvcs.ctx.getDefaultIoc();
        String annotated = element.getAttribute("annotated");
        if (Strings.isBlank(annotated)) {
            throw new RuntimeException("nutzioc:bridge need attr annotated");
        }
        String[] names = Strings.splitIgnoreBlank(annotated); 
        for (String name : ioc.getNames()) {
            try {
                Class<?> t = ioc.getType(name);
                if (t == null)
                    continue;
                if (t.getName().endsWith(ClassAgent.CLASSNAME_SUFFIX)) {
                    t = t.getSuperclass();
                }
                Annotation[] annos = t.getAnnotations();
                boolean flag = false;
                for (String _name : names) {
                    for (Annotation anno : annos) {
                        if (anno.annotationType().getName().equals(_name)) {
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    log.debugf("proxy [%s] into spring ioc context", name);
                    RootBeanDefinition beanDefinition = new RootBeanDefinition();
                    beanDefinition.setBeanClass(NutIocSpringBridgeBeanFactory.class);
                    beanDefinition.getPropertyValues().add("ioc", ioc);
                    beanDefinition.setLazyInit(false);
                    beanDefinition.setTargetType(t);
                    parserContext.getRegistry().registerBeanDefinition(name, beanDefinition);
                }
            }
            catch (ObjectLoadException e) {
                // nop
            }
        }
        return null;
    }

    

}
