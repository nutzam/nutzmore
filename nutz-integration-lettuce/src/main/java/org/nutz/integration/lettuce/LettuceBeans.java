package org.nutz.integration.lettuce;

import java.util.concurrent.TimeUnit;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import io.lettuce.core.RedisClient;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;

@IocBean(depose="depose")
public class LettuceBeans {

    @Inject
    protected PropertiesProxy conf;
    
    @Inject("refer:$ioc")
    protected Ioc ioc;
    
    protected ClientResources clientResources;
    
    @IocBean
    public RedisClient createRedisClient() {
        if (ioc.has("clientResources")) {
            clientResources = ioc.get(ClientResources.class);
        }
        else {
            // TODO 支持更详细的配置
            clientResources = DefaultClientResources.create();
        }
        return RedisClient.create(clientResources, conf.get("lettuce.url", "redis://127.0.0.1:6379/0"));
    }
    
    public void depose() throws InterruptedException {
        if (clientResources != null)
            clientResources.shutdown(1, 15, TimeUnit.SECONDS).await();
    }
}
