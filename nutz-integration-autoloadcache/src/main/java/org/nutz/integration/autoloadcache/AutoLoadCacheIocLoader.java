package org.nutz.integration.autoloadcache;

import org.nutz.ioc.loader.annotation.AnnotationIocLoader;

/**
 * @author Rekoe(koukou890@gmail.com)
 *
 */
public class AutoLoadCacheIocLoader extends AnnotationIocLoader {

    public AutoLoadCacheIocLoader() {
        super(AutoLoadCacheIocLoader.class.getPackage().getName());
    }

}
