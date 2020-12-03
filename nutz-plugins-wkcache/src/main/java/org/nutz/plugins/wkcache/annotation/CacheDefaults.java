package org.nutz.plugins.wkcache.annotation;

import java.lang.annotation.*;

/**
 * Created by wizzer on 2017/6/14.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheDefaults {
    String cacheName() default "wk";

    int cacheLiveTime() default 0;

    /**
     * 是否采用hash缓存方式(不支持失效时间,但时候缓存数量比较大的情况)
     */
    boolean isHash() default false;
}
