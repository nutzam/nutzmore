package org.nutz.integration.jedis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;

public class JedisAgentFactoryBean implements FactoryBean<JedisAgent>, BeanFactoryPostProcessor {
    
    protected String mode;
    
    protected JedisAgent jedisAgent;

    public JedisAgent getObject() throws Exception {
        return jedisAgent;
    }

    public Class<?> getObjectType() {
        return JedisAgent.class;
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
            jedisAgent = new JedisAgent(beanFactory.getBean(JedisCluster.class));
        } else if ("sentinel".equals(mode)) {
            jedisAgent = new JedisAgent(beanFactory.getBean(JedisSentinelPool.class));
        } else {
            jedisAgent = new JedisAgent(beanFactory.getBean(JedisPool.class));
        }
    }

}
