package org.nutz.integration.jedisque;

import org.nutz.ioc.loader.json.JsonLoader;

public class JedisqueIocLoader extends JsonLoader {

    public JedisqueIocLoader() {
        super("org/nutz/integration/jedisque/jedisque.js");
    }
}
