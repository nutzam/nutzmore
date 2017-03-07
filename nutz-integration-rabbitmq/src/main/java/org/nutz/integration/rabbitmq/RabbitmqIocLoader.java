package org.nutz.integration.rabbitmq;

import org.nutz.ioc.loader.json.JsonLoader;

public class RabbitmqIocLoader extends JsonLoader {

    public RabbitmqIocLoader() {
        super("org/nutz/integration/rabbitmq/rabbitmq.js");
    }
}
