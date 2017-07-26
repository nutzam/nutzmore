package org.nutz.plugins.view.beetl;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Resource;
import org.beetl.core.ResourceLoader;
import org.beetl.core.resource.FileResource;
import org.beetl.core.resource.FileResourceLoader;
import org.nutz.lang.Encoding;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

public class WebappResourceLoader2 implements ResourceLoader {
    
    private static final Log log = Logs.get();
    
    protected ServletContext sc;
    
    protected String root;
    
    protected FileResourceLoader fileResourceLoader;

    public void close() {
    }

    public boolean exist(String path) {
        try {
            return getServletContext().getResource(root + path) != null;
        }
        catch (MalformedURLException e) {
            return false;
        }
    }

    @Override
    public String getInfo() {
        return "WebappResourceLoader2,Root=" + this.root;
    }

    @Override
    public Resource getResource(final String path) {
        try {
            final String src = _path(root + path);
            String file = getServletContext().getRealPath(src);
            if (file != null)
                return new FileResource(new File(file), path, fileResourceLoader);
            URL url = getServletContext().getResource(src);
            if (url != null) {
                return new Resource(path, this) {
                    
                    public Reader openReader() {
                        return new InputStreamReader(getServletContext().getResourceAsStream(src), Encoding.CHARSET_UTF8);
                    }
                    
                    public boolean isModified() {
                        return false;
                    }
                };
            }
        }
        catch (Exception e) {
            log.infof("bad beetl template path? root=%s path=%s", root, path);
        }
        return null;
    }

    @Override
    public String getResourceId(Resource resource, String id) {
        if (resource == null || id.startsWith("/"))
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

    @Override
    public void init(GroupTemplate gt) {
        root = gt.getConf().getResourceMap().get("root");
        if (Strings.isBlank(root)) {
            root = "/WEB-INF/templates/";
        }
        else if (!root.endsWith("/")) {
            root += "/";
        }
        String path = getServletContext().getRealPath(root);
        if (path != null)
            fileResourceLoader = new FileResourceLoader(path);
        log.debug("Use Beetl Template Root= " + root);
    }

    public boolean isModified(Resource resource) {
        if (resource instanceof FileResource)
            return ((FileResource)resource).isModified();
        return false;
    }

    ServletContext getServletContext() {
        if (sc == null)
            sc = Mvcs.getServletContext();
        return sc;
    }
    
    public String _path(String path) {
        return path.replace("//", "/");
    }
}
