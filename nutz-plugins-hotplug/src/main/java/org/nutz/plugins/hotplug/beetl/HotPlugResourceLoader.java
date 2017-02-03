package org.nutz.plugins.hotplug.beetl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.beetl.core.Resource;
import org.beetl.core.resource.FileResource;
import org.beetl.core.resource.StringTemplateResource;
import org.beetl.core.resource.WebAppResourceLoader;
import org.nutz.lang.Encoding;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.hotplug.HotPlug;
import org.nutz.plugins.hotplug.HotPlugConfig;

public class HotPlugResourceLoader extends WebAppResourceLoader {

    private static final Log log = Logs.get();

    public HotPlugResourceLoader() {
        super();
    }

    public HotPlugResourceLoader(String root, String charset) {
        super(root, charset);
    }

    public HotPlugResourceLoader(String root) {
        super(root);
    }

    public Resource getResource(String key) {
        String tmp = key;
        if (tmp.startsWith("/"))
            tmp = tmp.substring(1);
        File f = HotPlug.find("templates/"+tmp);
        if (f != null)
            return new FileResource(f, key, this);
        // 从插件里面找找呗
        for (HotPlugConfig hc : HotPlug.plugins.values()) {
            String tmpl = hc.getTmpls().get(tmp);
            if (tmpl != null) {
                return new StringTemplateResource(tmpl, this);
            }
        }
        // 在ClassPath里面找
        final URL url = getClass().getClassLoader().getResource("templates/" + key);
        if (url != null) {
            log.debugf("found %s", url.toExternalForm());
            return new Resource(key, this) {

                public Reader openReader() {
                    try {
                        return new InputStreamReader(url.openStream(), Encoding.CHARSET_UTF8);
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                public boolean isModified() {
                    return false;
                }
            };
        }
        return super.getResource(key);
    }

    @Override
    public String getResourceId(Resource resource, String id) {
        return super.getResourceId(resource, id);
    }
}
