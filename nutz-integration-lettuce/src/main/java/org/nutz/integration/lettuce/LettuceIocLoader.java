package org.nutz.integration.lettuce;

import org.nutz.ioc.loader.annotation.AnnotationIocLoader;

public class LettuceIocLoader extends AnnotationIocLoader {

    public LettuceIocLoader() {
        super("org.nutz.integration.lettuce");
    }
}
