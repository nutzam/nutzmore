package org.nutz.plugins.slog;

import org.nutz.ioc.loader.json.JsonLoader;

/**
 * 加载slog.js
 * @author wendal
 *
 */
public class SlogIocLoader extends JsonLoader {

    public SlogIocLoader() {
        super("org/nutz/plugins/slog/slog.js");
    }
}
