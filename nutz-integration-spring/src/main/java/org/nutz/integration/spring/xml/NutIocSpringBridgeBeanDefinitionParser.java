package org.nutz.integration.spring.xml;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.mvc.Mvcs;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

public class NutIocSpringBridgeBeanDefinitionParser implements BeanDefinitionParser {

    @SuppressWarnings("deprecation")
    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        Ioc ioc = Mvcs.ctx.getDefaultIoc();
        for (String name : ioc.getNames()) {
            try {
                Class<?> t = ioc.getType(name);
                if (t == null)
                    continue;
                RootBeanDefinition beanDefinition = new RootBeanDefinition();
                beanDefinition.setBeanClass(NutIocSpringBridgeBeanFactory.class);
                beanDefinition.getPropertyValues().add("ioc", ioc);
                beanDefinition.setLazyInit(false);
                beanDefinition.setTargetType(t);
                parserContext.getRegistry().registerBeanDefinition(name, beanDefinition);
            }
            catch (ObjectLoadException e) {
                // nop
            }
        }
        return null;
    }

    

}
