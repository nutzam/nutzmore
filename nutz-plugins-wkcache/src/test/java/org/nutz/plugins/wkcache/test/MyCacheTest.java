package org.nutz.plugins.wkcache.test;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemove;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

/**
 * Created by wizzer on 2017/6/14.
 */
@CacheDefaults(cacheName = "nutzwk_cache")
@IocBean
public class MyCacheTest {
    @CacheResult(cacheKey = "test")
    public Object testCache(String txt) {
        return txt;
    }

    @CacheRemove(cacheKey = "test")
    public void testRemove() {

    }

    @CacheRemoveAll()
    public void testRemoveAll() {

    }
}
