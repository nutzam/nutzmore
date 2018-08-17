package org.nutz.plugins.wkcache.test;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.wkcache.annotation.*;

import java.util.List;

/**
 * Created by wizzer on 2017/6/14.
 */
@CacheDefaults(cacheName = "nutzwk_cache",cacheLiveTime = 3000)
@IocBean
public class MyCacheTest {
    @CacheResult(cacheName = "cache_time_1",cacheKey = "test")
    public Object cache_time_1(String txt) {
        return txt;
    }

    @CacheResult(cacheName = "cache_time_2",cacheKey = "test")
    public Object cache_time_2(String txt) {
        return txt;
    }

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
        System.out.println("我被执行了...");
        return test;
    }

    @CacheResult
    public Object testCacheList(List test) {
        System.out.println("我被执行了...");
        return test;
    }

    @CacheUpdate(cacheKey = "hello")
    public Object testUpdate(String txt) {
        return txt;
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
