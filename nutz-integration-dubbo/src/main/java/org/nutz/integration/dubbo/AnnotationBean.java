package org.nutz.integration.dubbo;

import java.lang.reflect.Field;
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
    
    public void setPackage(String annotationPackage) {
    	this.annotationPackage = annotationPackage;
    }
    
    public void _init() {
        Set<Field> consumers = new HashSet<>();
        Set<Class> services = new HashSet<>();
        for (String pkg : annotationPackages == null ? Strings.splitIgnoreBlank(annotationPackage, ",") : annotationPackages) {
            for (Class klass :Scans.me().scanPackage(pkg)) {
                if (klass.isInterface()) {
                    continue;
                }
                Service service = (Service) klass.getAnnotation(Service.class);
                if (service != null)
                    services.add(klass);
                for (Field field : klass.getDeclaredFields()) {
                	if (field.getType().isInterface() && field.getAnnotation(Reference.class) != null) {
                		consumers.add(field);
                	}
                }
            }
        }
        // 注册消费者为ioc bean
        for (Field field : consumers) {
            Reference ref = (Reference) field.getAnnotation(Reference.class);
            ReferenceBean<?> rc = new ReferenceBean<>(ref);
            rc.setInterface(field.getType());
            String name = R.UU32();
            IocObject iobj = Iocs.wrap(rc);
            iobj.setType(ReferenceBean.class);
            DubboAgent.checkIocObject(name, iobj);
            iobjs.put(name, iobj);
            
            iobj = new IocObject();
            iobj.setType(field.getType());
            iobj.setFactory("$" + name + "#get");
            iobjs.put(R.UU32(), iobj);
        }
        // 导出生产者
        for (Class klass : services) {
            Service service = (Service) klass.getAnnotation(Service.class);
            ServiceBean sc = new ServiceBean<>(service);
            sc.setRef(ioc.getByType(klass));
            
            String name = R.UU32();
            IocObject iobj = Iocs.wrap(sc);
            iobj.setType(ServiceBean.class);
            DubboAgent.checkIocObject(name, iobj);
            iobjs.put(name, iobj);
        }
    }
    
    public void depose(){}
    
    public void setPackages(String[] annotationPackages) {
		this.annotationPackages = annotationPackages;
	}
}
