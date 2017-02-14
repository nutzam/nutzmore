package org.nutz.plugins.hotplug;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;

public class HotplugIocLoader implements IocLoader {

    public String[] getName() {
        List<String> names = new ArrayList<String>();
        for (HotplugConfig hc : Hotplug.getActiveHotPlugList()) {
            if (hc.iocLoader != null) {
                names.addAll(Arrays.asList(hc.iocLoader.getName()));
            }
        }
        return names.toArray(new String[names.size()]);
    }

    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        for (HotplugConfig hc : Hotplug.getActiveHotPlugList()) {
            if (hc.iocLoader != null) {
                if (hc.iocLoader.has(name))
                    return hc.iocLoader.load(loading, name);
            }
        }
        throw new ObjectLoadException("Object '" + name + "' without define! Pls check your ioc configure");
    }

    public boolean has(String name) {
        for (HotplugConfig hc : Hotplug.getActiveHotPlugList()) {
            if (hc.iocLoader != null) {
                if (hc.iocLoader.has(name))
                    return true;
            }
        }
        return false;
    }
    
    public String toString() {
        return "/*hotplug*/{}";
    }

}
