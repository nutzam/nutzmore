package org.nutz.plugins.xmlbind.entity;

import org.nutz.lang.eject.Ejecting;
import org.nutz.lang.inject.Injecting;

public class XmlEntityAttr {

    protected String name;
    protected boolean ignoreZero;
    protected boolean ignoreBlank;
    protected transient Ejecting ejecting;
    protected transient Injecting injecting;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isIgnoreZero() {
        return ignoreZero;
    }
    public void setIgnoreZero(boolean ignoreZero) {
        this.ignoreZero = ignoreZero;
    }
    public boolean isIgnoreBlank() {
        return ignoreBlank;
    }
    public void setIgnoreBlank(boolean ignoreBlank) {
        this.ignoreBlank = ignoreBlank;
    }
    public Ejecting getEjecting() {
        return ejecting;
    }
    public void setEjecting(Ejecting ejecting) {
        this.ejecting = ejecting;
    }
    public Injecting getInjecting() {
        return injecting;
    }
    public void setInjecting(Injecting injecting) {
        this.injecting = injecting;
    }
}
