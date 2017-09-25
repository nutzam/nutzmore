package org.nutz.plugins.sqltpl.impl.beetl;

import org.beetl.core.Resource;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.core.resource.FileResource;
import org.beetl.core.resource.StringTemplateResource;

public class ClasspathStringResourceLoader extends ClasspathResourceLoader {
    
    public ClasspathStringResourceLoader() {
        super();
    }

    public ClasspathStringResourceLoader(ClassLoader classLoader, String root, String charset) {
        super(classLoader, root, charset);
    }

    public ClasspathStringResourceLoader(ClassLoader classLoader, String root) {
        super(classLoader, root);
    }

    public ClasspathStringResourceLoader(ClassLoader classLoader) {
        super(classLoader);
    }

    public ClasspathStringResourceLoader(String root, String charset) {
        super(root, charset);
    }

    public ClasspathStringResourceLoader(String root) {
        super(root);
    }

    public Resource getResource(String key) {
        if (key.startsWith("/"))
            return super.getResource(key);
        return new StringTemplateResource(key, this);
    }

    public boolean isModified(Resource key) {
        if (key instanceof FileResource)
            return super.isModified(key);
        return false;
    }

    public boolean exist(String key) {
        if (key.startsWith("/"))
            return super.exist(key);
        return true;
    }

    public void close() {
        super.close();
    }

}
