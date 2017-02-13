package org.nutz.plugins.hotplug;

import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.UrlMapping;
import org.nutz.mvc.impl.ActionInvoker;

public class HotPlugUrlMapping implements UrlMapping {
    
    private static final Log log = Logs.get();
    
    protected UrlMapping main;
    
    public HotPlugUrlMapping(UrlMapping main, ServletContext sc) {
        this.main = main;
    }

    public void add(ActionChainMaker maker, ActionInfo ai, NutConfig config) {
        main.add(maker, ai, config);
    }

    public ActionInvoker get(ActionContext ac) {
        // 遍历插件,看看有无合适的映射
        for (Entry<String, HotPlugConfig> en : HotPlug.getActiveHotPlug().entrySet()) {
            String key = en.getKey();
            UrlMapping mapping = en.getValue().urlMapping;
            if (mapping != null) {
                ActionInvoker ai = mapping.get(ac);
                if (ai != null) {
                    log.debugf("found mapping at plugin(%s)", key);
                    return ai;
                }
            }
        }
        return main.get(ac);
    }
    public void add(String path, ActionInvoker invoker) {
        main.add(path, invoker);
    }

}