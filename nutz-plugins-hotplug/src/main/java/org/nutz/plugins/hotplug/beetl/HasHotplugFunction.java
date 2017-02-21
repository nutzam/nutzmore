package org.nutz.plugins.hotplug.beetl;

import org.beetl.core.Context;
import org.beetl.core.Function;
import org.nutz.plugins.hotplug.Hotplug;

public class HasHotplugFunction implements Function {

    public Object call(Object[] paras, Context ctx) {
        return Hotplug.getActiveHotPlug().containsKey((String)paras[0]);
    }

}
