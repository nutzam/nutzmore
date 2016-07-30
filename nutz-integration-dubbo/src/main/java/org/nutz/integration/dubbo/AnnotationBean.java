package org.nutz.integration.dubbo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nutz.ioc.Ioc;
import org.nutz.ioc.Iocs;
import org.nutz.ioc.meta.IocObject;
import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.resource.Scans;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;

@SuppressWarnings({"unchecked", "rawtypes"})
public class AnnotationBean {

    protected Ioc ioc;
    
    protected Map<String, IocObject> iobjs;
    
    protected String beanName;
    
    private String annotationPackage;

    private String[] annotationPackages;
    
    public void _init() {
        Set<Class> consumers = new HashSet<>();
        Set<Class> services = new HashSet<>();
        for (String pkg : annotationPackages == null ? Strings.splitIgnoreBlank(annotationPackage, ",") : annotationPackages) {
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
            ReferenceBean<?> rc = new ReferenceBean<>(ref);
            rc.setInterface(klass);
            String name = R.UU32();
            IocObject iobj = Iocs.wrap(rc);
            iobjs.put(name, iobj);
        }
        // 导出生产者
        for (Class klass : services) {
            Service service = (Service) klass.getAnnotation(Service.class);
            ServiceBean sc = new ServiceBean<>(service);
            sc.setRef(ioc.get(klass));
        }
    }
    
    public void depose(){}
}
