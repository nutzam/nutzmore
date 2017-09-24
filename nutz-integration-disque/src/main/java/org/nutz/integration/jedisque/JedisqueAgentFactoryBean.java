package org.nutz.integration.jedisque;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisqueAgentFactoryBean implements FactoryBean<JedisqueAgent>, BeanFactoryPostProcessor {
    
    protected String mode;
    
    protected JedisqueAgent jedisAgent;

    public JedisqueAgent getObject() throws Exception {
        return jedisAgent;
    }

    public Class<?> getObjectType() {
        return JedisqueAgent.class;
    }

    public boolean isSingleton() {
        return true;
    }
    
    public void setMode(String mode) {
        this.mode = mode;
    }

    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
        if ("cluster".equals(mode)) {
            jedisAgent = new JedisqueAgent(beanFactory.getBean(JedisCluster.class));
        } else {
            jedisAgent = new JedisqueAgent(beanFactory.getBean(JedisPool.class));
        }
    }

}
