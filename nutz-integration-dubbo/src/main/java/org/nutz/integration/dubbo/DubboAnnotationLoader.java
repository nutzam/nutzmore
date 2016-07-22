package org.nutz.integration.dubbo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.IocLoader;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Strings;
import org.nutz.resource.Scans;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;

@SuppressWarnings({"unchecked", "rawtypes"})
public class DubboAnnotationLoader implements IocLoader {

    protected String packages;
    
    protected ApplicationConfig application;
    
    protected RegistryConfig registry;
    
    protected Map<String, ReferenceConfig> map;
    
    protected Ioc ioc;

    public String[] getName() {
        _load();
        return map.keySet().toArray(new String[map.size()]);
    }

    public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
        _load();
        ReferenceConfig rc = map.get(name);
        if (rc == null)
            throw new ObjectLoadException("Object '" + name + "' without define!");
        return Iocs.wrap(rc.get());
    }

    public boolean has(String name) {
        _load();
        return map.containsKey(name);
    }
    
    protected synchronized void _load() {
        if (this.map != null)
            return;
        Map<String, ReferenceConfig> map = new HashMap<>();
        Set<Class> consumers = new HashSet<>();
        Set<Class> services = new HashSet<>();
        for (String pkg : Strings.splitIgnoreBlank(packages, ",")) {
            for (Class klass :Scans.me().scanPackage(pkg)) {
                if (klass.isInterface()) {
                    Reference ref = (Reference) klass.getAnnotation(Reference.class);
                    if (ref != null)
                        consumers.add(klass);
                }
                Service service = (Service) klass.getAnnotation(Service.class);
                if (service != null)
                    services.add(klass);
            }
        }
        // 注册消费者为ioc bean
        for (Class<?> klass : consumers) {
            Reference ref = (Reference) klass.getAnnotation(Reference.class);
            ReferenceConfig rc = new ReferenceConfig<>(ref);
            rc.setInterface(klass);
            rc.setApplication(application);
            rc.setRegistry(registry);
            map.put(ref.consumer(), rc);
        }
        // 导出生产者
        for (Class klass : services) {
            Service service = (Service) klass.getAnnotation(Service.class);
            ServiceConfig sc = new ServiceConfig<>(service);
            sc.setRef(ioc.get(klass));
            if (sc.getRegistry() == null)
                sc.setRegistry(registry);
            if (sc.getApplication() == null)
                sc.setApplication(application);
            if (sc.isExported())
                sc.export();
        }
        this.map = map;
    }
    
}
