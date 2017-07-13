package org.nutz.integration.shiro;

import org.nutz.ioc.loader.json.JsonLoader;

public class ShiroIocLoader extends JsonLoader {

    public ShiroIocLoader() {
        super("org/nutz/integration/shiro/shiro.js");
    }
}
