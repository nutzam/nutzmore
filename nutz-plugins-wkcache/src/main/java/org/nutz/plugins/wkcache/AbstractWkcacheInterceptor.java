package org.nutz.plugins.wkcache;

import org.nutz.aop.MethodInterceptor;
import org.nutz.integration.jedis.JedisAgent;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

@IocBean
public abstract class AbstractWkcacheInterceptor implements MethodInterceptor {

    @Inject("refer:$ioc")
    protected Ioc ioc;
    private JedisAgent jedisAgent;
    private PropertiesProxy conf;

    public void setIoc(Ioc ioc) {
        this.ioc = ioc;
    }

    protected JedisAgent getJedisAgent() {
        if (jedisAgent == null)
            jedisAgent = ioc.get(JedisAgent.class);
        return jedisAgent;
    }

    protected PropertiesProxy getConf() {
        if (conf == null)
            conf = ioc.get(PropertiesProxy.class, "conf");
        return conf;
    }
}
