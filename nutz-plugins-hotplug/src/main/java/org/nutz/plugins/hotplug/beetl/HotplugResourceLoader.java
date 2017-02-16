package org.nutz.plugins.hotplug.beetl;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.beetl.core.Resource;
import org.beetl.core.resource.FileResource;
import org.beetl.core.resource.WebAppResourceLoader;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.hotplug.Hotplug;
import org.nutz.plugins.hotplug.HotplugConfig;

public class HotplugResourceLoader extends WebAppResourceLoader {

    private static final Log log = Logs.get();

    public HotplugResourceLoader() {
        super();
    }

    public HotplugResourceLoader(String root, String charset) {
        super(root, charset);
    }

    public HotplugResourceLoader(String root) {
        super(root);
    }

    public Resource getResource(String key) {
        String tmp = key;
        if (tmp.startsWith("/"))
            tmp = tmp.substring(1);
        File f = Hotplug.find("templates/"+tmp);
        if (f != null)
            return new FileResource(f, key, this);
        // 从插件里面找找呗
        for (HotplugConfig hc : Hotplug.getActiveHotPlugList()) {
            final String tmpl = hc.getTmpls().get(tmp);
            if (tmpl != null) {
                return new Resource(key, this) {

                    public Reader openReader() {
                        return new StringReader(tmpl);
                    }

                    public boolean isModified() {
                        return false;
                    }
                };
            }
        }
        // 在ClassPath里面找
        final URL url = getClass().getClassLoader().getResource("templates/" + tmp);
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
        if (resource == null)
            return id;
        String[] myId = Strings.splitIgnoreBlank(resource.getId(), "[\\\\/]");
        String[] yourId = Strings.splitIgnoreBlank(id, "[\\\\/]");
        List<String> names = new ArrayList<String>();
        for (String name : myId) {
            names.add(name);
        }
        names.remove(names.size() - 1);//最后一个是文件名,去掉
        for (String name : yourId) {
            if ("..".equals(name)) {
                if (names.size() > 0)
                    names.remove(names.size()-1);
            }
            else {
                names.add(name);
            }
        }
        return "/"+Strings.join("/", names.toArray());
    }
}
