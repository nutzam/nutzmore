package org.nutz.plugins.wkcache;

import org.nutz.aop.MethodInterceptor;
import org.nutz.integration.jedis.RedisService;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.segment.CharSegment;

@IocBean
public abstract class AbstractWkcacheInterceptor implements MethodInterceptor {

    @Inject("refer:$ioc")
    protected Ioc ioc;
    private PropertiesProxy conf;
    protected CharSegment key;

    public void setIoc(Ioc ioc) {
        this.ioc = ioc;
    }

    protected RedisService redisService;

    protected RedisService redisService() {
        if (redisService == null)
            redisService = ioc.get(RedisService.class);
        return redisService;
    }

    protected PropertiesProxy getConf() {
        if (conf == null)
            conf = ioc.get(PropertiesProxy.class,"conf");
        return conf;
    }
}
