package org.nutz.integration.autoloadcache;

import com.jarvis.cache.CacheHandler;
import com.jarvis.cache.annotation.CacheDeleteTransactional;
import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.ioc.loader.annotation.IocBean;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;


@IocBean(name = "$aop_txdelcache")
public class TxDelCacheAopConfigration extends SimpleAopMaker<CacheDeleteTransactional> {

    @Override
    public List<? extends MethodInterceptor> makeIt(CacheDeleteTransactional cache, Method method, Ioc ioc) {
        return Arrays.asList(new TxDelCacheAopInterceptor(ioc.get(CacheHandler.class), cache, method));
    }
}
