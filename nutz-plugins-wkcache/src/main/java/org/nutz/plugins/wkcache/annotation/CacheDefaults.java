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
}
