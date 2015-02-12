package org.nutz.jcache;

import org.nutz.ioc.loader.annotation.AnnotationIocLoader;

public class NutCacheIocLoader extends AnnotationIocLoader {

    public NutCacheIocLoader() {
        super(NutCacheIocLoader.class.getPackage().getName());
    }

}
