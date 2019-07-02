package org.nutz.integration.redisson;

import org.nutz.ioc.loader.annotation.AnnotationIocLoader;

public class RedissonIocLoader extends AnnotationIocLoader {

    public RedissonIocLoader() {
        super("org.nutz.integration.redisson");
    }
}
