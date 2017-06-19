package org.nutz.plugins.wkcache;

import org.nutz.ioc.loader.annotation.AnnotationIocLoader;

/**
 * Created by wizzer on 2017/6/14.
 */
public class WkcacheIocLoader extends AnnotationIocLoader {

    public WkcacheIocLoader() {
        super(WkcacheIocLoader.class.getPackage().getName());
    }

}
