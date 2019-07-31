package org.nutz.plugins.view.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;

public class DynamicTemplateLoader implements TemplateLoader {
    
    private FileTemplateLoader fileLoader;
    private ClassTemplateLoader classLoader;
    
    public class Holder {
        private TemplateLoader loader;
        private Object obj;
        public Holder(TemplateLoader loader, Object obj) {
            this.loader = loader;
            this.obj = obj;
        }
    }
    
    public DynamicTemplateLoader(File filebase, String classbase) throws IOException {
        fileLoader = new FileTemplateLoader(filebase);
        classLoader = new ClassTemplateLoader(getClass().getClassLoader(), classbase);
    }

    @Override
    public Object findTemplateSource(String name) throws IOException {
        Object obj = fileLoader.findTemplateSource(name);
        if (obj != null) {
            return new Holder(fileLoader, obj);
        }
        obj = classLoader.findTemplateSource(name);
        if (obj != null)
            return new Holder(classLoader, obj);
        return null;
    }

    @Override
    public long getLastModified(Object templateSource) {
        return ((Holder)templateSource).loader.getLastModified(((Holder)templateSource).obj);
    }

    @Override
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return ((Holder)templateSource).loader.getReader(((Holder)templateSource).obj, encoding);
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {
        ((Holder)templateSource).loader.closeTemplateSource(((Holder)templateSource).obj);
    }

}
