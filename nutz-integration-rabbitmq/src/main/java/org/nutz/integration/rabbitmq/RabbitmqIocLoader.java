package org.nutz.integration.rabbitmq;

import org.nutz.ioc.loader.annotation.AnnotationIocLoader;

public class RabbitmqIocLoader extends AnnotationIocLoader {

    public RabbitmqIocLoader() {
        super(RabbitmqIocLoader.class.getPackage().getName());
    }
}
