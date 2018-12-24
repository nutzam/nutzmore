package org.nutz.integration.spring.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class NutIocSpringBridgeNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("bridge", new NutIocSpringBridgeBeanDefinitionParser());
    }

}
