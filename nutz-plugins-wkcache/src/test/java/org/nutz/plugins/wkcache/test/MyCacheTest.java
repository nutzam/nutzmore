package org.nutz.plugins.wkcache.test;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.CacheDefaults;
import org.nutz.plugins.wkcache.annotation.CacheRemove;
import org.nutz.plugins.wkcache.annotation.CacheRemoveAll;
import org.nutz.plugins.wkcache.annotation.CacheResult;

/**
 * Created by wizzer on 2017/6/14.
 */
@CacheDefaults(cacheName = "nutzwk_cache",cacheLiveTime = 3000)
@IocBean
public class MyCacheTest {
    @CacheResult(cacheKey = "test")
    public Object testCache(String txt) {
        return txt;
    }

    @CacheResult(cacheKey = "${args[0]}")
    public Object testCacheEl(String txt) {
        return txt;
    }

    @CacheResult(cacheKey = "${args[0].id}_${args[0].name}")
    public Object testCacheObj(TestBean test) {
        return test;
    }

    @CacheRemove(cacheKey = "test")
    public void testRemove() {

    }

    @CacheRemove(cacheKey = "${args[0].id}_${args[0].name}")
    public void testRemove(TestBean test) {

    }

    @CacheRemoveAll()
    public void testRemoveAll() {

    }
}
