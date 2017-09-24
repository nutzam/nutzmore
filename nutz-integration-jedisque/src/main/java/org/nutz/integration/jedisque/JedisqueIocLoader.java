package org.nutz.integration.jedisque;

import org.nutz.ioc.loader.json.JsonLoader;

/**
 * Created by Jianghao on 2017-09-24
 *
 * @howechiang
 */
public class JedisqueIocLoader extends JsonLoader {

    public JedisqueIocLoader() {
        super("org/nutz/integration/jedisque/jedisque.js");
    }
}
