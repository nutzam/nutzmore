package org.nutz.plugins.wkcache.annotation;

import java.lang.annotation.*;

/**
 * Created by wizzer on 2017/6/14.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface CacheResult {
    String cacheName() default "";

    String cacheKey() default "";

    int cacheLiveTime() default 0;

    /**
     * 是否忽略 Null 值，如果为 true，当结果为 Null 时不进行缓存
     */
    boolean ignoreNull() default false;
}
