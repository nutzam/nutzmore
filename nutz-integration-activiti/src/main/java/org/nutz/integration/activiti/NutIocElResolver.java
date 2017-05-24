package org.nutz.integration.activiti;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.javax.el.ELContext;
import org.activiti.engine.impl.javax.el.ELResolver;
import org.nutz.ioc.Ioc;

public class NutIocElResolver extends ELResolver {

    protected Ioc ioc;

    public NutIocElResolver(Ioc ioc) {
        this.ioc = ioc;
    }

    public Object getValue(ELContext context, Object base, Object property) {
        if (base == null) {
            String key = (String) property;
            if (ioc.has(key)) {
                context.setPropertyResolved(true);
                return ioc.get(null, key);
            }
        }
        return null;
    }

    public boolean isReadOnly(ELContext context, Object base, Object property) {
        return true;
    }

    public void setValue(ELContext context, Object base, Object property, Object value) {
        if (base == null) {
            String key = (String) property;
            if (ioc.has(key)) {
                throw new ActivitiException("Cannot set value of '"
                                            + property
                                            + "', it resolves to a bean defined in the NutIoc.");
            }
        }
    }

    public Class<?> getCommonPropertyType(ELContext context, Object arg) {
        return Object.class;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object arg) {
        return null;
    }

    public Class<?> getType(ELContext context, Object arg1, Object arg2) {
        return Object.class;
    }
}
