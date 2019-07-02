package org.nutz.integration.spring.xml;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.ObjectLoadException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;

public class NutIocSpringBridgeBeanFactory implements FactoryBean<Object>, BeanNameAware {

    public Object proxy;
    public Ioc ioc;
    public String beanName;

    public Object getObject() throws Exception {
        return ioc.get(null, beanName);
    }

    @Override
    public Class<?> getObjectType() {
        try {
            return ioc.getType(beanName);
        }
        catch (ObjectLoadException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }
    
    public void setIoc(Ioc ioc) {
        this.ioc = ioc;
    }
}
