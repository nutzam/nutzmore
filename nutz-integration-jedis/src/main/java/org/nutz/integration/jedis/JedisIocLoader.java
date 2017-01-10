package org.nutz.integration.jedis;

import org.nutz.ioc.loader.json.JsonLoader;

public class JedisIocLoader extends JsonLoader {

    public JedisIocLoader() {
        super("org/nutz/integration/jedis/jedis.js");
    }
}
