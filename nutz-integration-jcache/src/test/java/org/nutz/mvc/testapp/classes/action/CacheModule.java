package org.nutz.mvc.testapp.classes.action;

import javax.cache.annotation.CacheDefaults;
import javax.cache.annotation.CacheKey;
import javax.cache.annotation.CachePut;
import javax.cache.annotation.CacheRemove;
import javax.cache.annotation.CacheRemoveAll;
import javax.cache.annotation.CacheResult;
import javax.cache.annotation.CacheValue;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.random.R;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Ok;

/**
 * 注意, 这个模块演示的是jsr107的4个注解的用法,而非演示一个通用的cache接口
 * @author wendal(wendal1985@gmail.com)
 *
 */
@IocBean // 通过aop实现,所以当前类必须是ioc的bean.
        // 在主模块的@IocBy中, 通过ComboIocLoader的*cache参数启用下列注解
@At("/cache")
@CacheDefaults(cacheName="nutz_cache_test") // 定义下面4个方法注解所需要的默认参数, 这里声明的参数在各注解中仍可覆盖
@Ok("raw")
@Fail("raw")
public class CacheModule {

    /**
     * 执行一个方法,以方法签名和方法参数作为key, 方法返回值作为value, 存入缓存
     * @param key @CacheResult以方法的参数和方法签名做为key, 如果不需要全部参数作为key,那么需要@CacheKey注解
     * @return
     */
    @CacheResult
    @At("/query/?")
    public String query(String key) {
        return R.UU32();
    }
    
    /**
     * 删除一条缓存记录
     * @param key, 功能与@CacheResult一致
     */
    @CacheRemove
    @At("/del/?")
    public void del(String key) {
    }
    
    /**
     * 清除缓存
     */
    @CacheRemoveAll
    @At("/clear")
    public void clear(){
    }

    /**
     * 更新一条缓存
     * @param key 缓存所需要的key
     * @param value 更新的值
     */
    @CachePut
    @At("/put/?/?")
    public void put(@CacheKey String key, @CacheValue String value) {
    }
}
