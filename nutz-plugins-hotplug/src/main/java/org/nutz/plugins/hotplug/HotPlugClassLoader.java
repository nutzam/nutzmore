package org.nutz.plugins.hotplug;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class HotPlugClassLoader extends ClassLoader {
    
    protected ThreadLocal<Object> lock = new ThreadLocal<Object>();

    public HotPlugClassLoader(ClassLoader parent) {
        super(parent);
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            return getParent().loadClass(name);
        }
        catch (ClassNotFoundException e) {
            if (lock.get() != null)
                throw e;
        }
        lock.set(this);
        try {
            for (HotPlugConfig hc : HotPlug.getActiveHotPlug().values()) {
                try {
                    return hc.classLoader.loadClass(name);
                }
                catch (ClassNotFoundException e) {
                }
            }
        } finally {
            lock.remove();
        }
        throw new ClassNotFoundException(name);
    }
    
    protected Enumeration<URL> findResources(String name) throws IOException {
        return getParent().getResources(name);
    }
    
    protected URL findResource(String name) {
        return getParent().getResource(name);
    }
}
