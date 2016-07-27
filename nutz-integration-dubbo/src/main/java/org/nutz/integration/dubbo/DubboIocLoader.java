package org.nutz.integration.dubbo;

import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocEventSet;
import org.nutz.ioc.meta.IocObject;
import org.nutz.mvc.Mvcs;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.ServiceConfig;

public class DubboIocLoader implements IocLoader {
    
    DubboConfigureReader reader;
    
    protected DubboIocLoader() {}
    
    public DubboIocLoader(String xmlpath) {
        reader = new DubboConfigureReader(xmlpath);
    }

    public String[] getName() {
        int count = reader.maps.size();
        return reader.maps.keySet().toArray(new String[count]);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        Object obj = reader.maps.get(name);
        if (obj == null)
            throw new ObjectLoadException("Object '" + name + "' without define!");
        if (obj instanceof ServiceConfig) {
            ServiceConfig sc = (ServiceConfig)obj;
            Object ref = sc.getRef();
            if (ref instanceof String) {
                ref = Mvcs.getIoc().get(sc.getInterfaceClass(), (String)ref);
                sc.setRef(ref);
            }
        }
        // 包裹成一个Bean
        IocObject iobj = Iocs.wrap(obj);
        if (obj instanceof ServiceConfig) {
            // 需要导出服务
            IocEventSet events = new IocEventSet();
            events.setCreate("export");
            events.setDepose("unexport");
            iobj.setEvents(events);
        } else if (obj instanceof ReferenceConfig) {
            // 关闭服务时需要销毁
            IocEventSet events = new IocEventSet();
            events.setDepose("destroy");
            iobj.setEvents(events);
        }
        return iobj;
    }

    public boolean has(String name) {
        return reader.maps.containsKey(name);
    }

}
