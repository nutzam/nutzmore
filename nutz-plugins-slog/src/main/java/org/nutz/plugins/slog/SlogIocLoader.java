package org.nutz.plugins.slog;

import org.nutz.ioc.loader.json.JsonLoader;

public class SlogIocLoader extends JsonLoader {

    public SlogIocLoader() {
        super("org/nutz/plugins/slog/slog.js");
    }
}
