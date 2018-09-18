package org.nutz.integration.lettuce;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

@IocBean(name="lettuce")
public class LettuceInterceptor implements MethodInterceptor {

    @Inject
    protected RedisClient redisClient;
    
    protected static ThreadLocal<StatefulRedisConnection<String, String>> TL = new ThreadLocal<StatefulRedisConnection<String, String>>();
    
    public void filter(InterceptorChain chain) throws Throwable {
        if (TL.get() != null) {
            chain.doChain();
            return;
        }
        StatefulRedisConnection<String, String> lettuce = null;
        try {
            lettuce = redisClient.connect();
            TL.set(lettuce);
            chain.doChain();
        } finally{
            if (lettuce != null) {
                try {
                    lettuce.close();
                }
                catch (Throwable e) {
                }
            }
            TL.remove();
        }
    }
    

    public static StatefulRedisConnection<String, String> lettuce() {
        return TL.get();
    }
    
    public void setRedisClient(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

}
