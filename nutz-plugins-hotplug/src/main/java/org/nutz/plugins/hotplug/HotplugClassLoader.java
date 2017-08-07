package org.nutz.plugins.hotplug;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

public class HotplugClassLoader extends ClassLoader {
    
    protected ThreadLocal<Object> lock = new ThreadLocal<Object>();

    public HotplugClassLoader(ClassLoader parent) {
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
            for (HotplugConfig hc : Hotplug.getActiveHotPlugList()) {
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
        if (lock.get() != null)
            return new Vector<URL>().elements();
        Vector<URL> vector = new Vector<URL>();
        Enumeration<URL> en = getParent().getResources(name);
        while (en.hasMoreElements()) {
            vector.add(en.nextElement());
        }
        lock.set(this);
        try {
            for (HotplugConfig hc : Hotplug.getActiveHotPlugList()) {
                en = hc.classLoader.getResources(name);
                while (en.hasMoreElements())
                    vector.add(en.nextElement());
            }
        } finally {
            lock.remove();
        }
        //log.debugf("name=%s size=%s", name, vector.size());
        return vector.elements();
    }
    
    protected URL findResource(String name) {
        try {
            Enumeration<URL> en = this.getResources(name);
            if (en.hasMoreElements())
                return en.nextElement();
            return null;
        }
        catch (IOException e) {
            return null;
        }
    }
    
}
