package org.nutz.plugins.hotplug;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.RequestPath;
import org.nutz.mvc.UrlMapping;
import org.nutz.mvc.impl.ActionInvoker;
import org.nutz.mvc.impl.MappingNode;
import org.nutz.mvc.impl.UrlMappingImpl;

public class HotplugUrlMapping implements UrlMapping {
    
    private static final Log log = Logs.get();
    
    protected UrlMapping main;
    
    public HotplugUrlMapping(UrlMapping main, ServletContext sc) {
        this.main = main;
    }

    public void add(ActionChainMaker maker, ActionInfo ai, NutConfig config) {
        main.add(maker, ai, config);
    }

    @SuppressWarnings("unchecked")
    public ActionInvoker get(ActionContext ac) {
        RequestPath rp = Mvcs.getRequestPathObject(ac.getRequest());
        String path = rp.getPath();
        //if (prefix != null)
        //    path = path.substring(prefix.length());
        ac.setSuffix(rp.getSuffix());
        Map<String, HotplugConfig> hotplugs = Hotplug.getActiveHotPlug();
        // 遍历插件,看看有无合适的映射
        for (Entry<String, HotplugConfig> en : hotplugs.entrySet()) {
            HotplugConfig hc = en.getValue();
            UrlMapping mapping = hc.urlMapping;
            if (mapping != null) {
                if (mapping instanceof UrlMappingImpl) {
                    if (hc.mapping_root == null) {
                        hc.mapping_root = (MappingNode<ActionInvoker>) Mirror.me(UrlMappingImpl.class).getValue(mapping, "root");
                    }
                    if (hc.mapping_root != null) {
                        ActionInvoker invoker = hc.mapping_root.get(ac, path);
                        if (invoker != null) {
                            ActionChain chain = invoker.getActionChain(ac);
                            if (chain != null) {
                                if (log.isDebugEnabled()) {
                                    log.debugf("Found mapping at hotplug(%s) for [%s] path=%s : %s",
                                               en.getKey(),
                                               ac.getRequest().getMethod(),
                                               path,
                                               chain);
                                }
                                return invoker;
                            }
                        }
                    }
                }
            }
        }
        return main.get(ac);
    }
    public void add(String path, ActionInvoker invoker) {
        main.add(path, invoker);
    }

}