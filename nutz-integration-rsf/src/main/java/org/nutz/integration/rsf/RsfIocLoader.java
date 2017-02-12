package org.nutz.integration.rsf;

import org.nutz.ioc.loader.json.JsonLoader;

public class RsfIocLoader extends JsonLoader {

    public RsfIocLoader() {
        super("org/nutz/integration/rsf/rsf.js");
    }
}
