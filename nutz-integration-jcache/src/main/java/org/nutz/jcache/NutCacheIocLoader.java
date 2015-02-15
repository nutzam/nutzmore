package org.nutz.jcache;

import org.nutz.ioc.loader.annotation.AnnotationIocLoader;

/**
 * 在1.b.52中,本loader的短名词为*cache, 会加载本package下方法拦截器及aop配置
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class NutCacheIocLoader extends AnnotationIocLoader {

    public NutCacheIocLoader() {
        super(NutCacheIocLoader.class.getPackage().getName());
    }

}
