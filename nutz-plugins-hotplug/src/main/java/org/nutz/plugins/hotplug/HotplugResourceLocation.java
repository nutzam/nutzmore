package org.nutz.plugins.hotplug;

import java.util.List;
import java.util.regex.Pattern;

import org.nutz.resource.NutResource;
import org.nutz.resource.impl.ResourceLocation;

public class HotplugResourceLocation extends ResourceLocation {

    public String id() {
        return "hotplug";
    }

    public void scan(String base, Pattern pattern, List<NutResource> list) {
        for (HotPlugConfig hc : HotPlug.getActiveHotPlug().values()) {
            ResourceLocation rc = hc.getResourceLocation();
            if (rc == null)
                continue;
            rc.scan(base, pattern, list);
        }
    }

}
