package org.nutz.integration.jsf;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELException;
import javax.el.ELResolver;
import javax.el.PropertyNotWritableException;

import org.nutz.ioc.Ioc;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;

public class NutElResorver extends ELResolver {

    private static final Log log = Logs.get();

    public Object getValue(ELContext elContext, Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            if (ioc().has(beanName)) {
                if (log.isTraceEnabled()) {
                    log.trace("Successfully resolved variable '" + beanName + "' in NutIoc");
                }
                elContext.setPropertyResolved(true);
                return ioc().get(null, beanName);
            }
        }
        return null;
    }

    public Class<?> getType(ELContext elContext, Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            if (ioc().has(beanName)) {
                elContext.setPropertyResolved(true);
                return ioc().get(null, beanName).getClass();
            }
        }
        return null;
    }

    public void setValue(ELContext elContext, Object base, Object property, Object value) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            if (ioc().has(beanName)) {
                throw new PropertyNotWritableException(
                        "Variable '" + beanName + "' refers to a NutIoc bean which by definition is not writable");
            }
        }
    }

    public boolean isReadOnly(ELContext elContext, Object base, Object property) throws ELException {
        if (base == null) {
            String beanName = property.toString();
            return ioc().has(beanName);
        }
        return false;
    }

    public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext elContext, Object base) {
        return null;
    }

    public Class<?> getCommonPropertyType(ELContext elContext, Object base) {
        return Object.class;
    }

    public Ioc ioc() {
        return Mvcs.ctx().getDefaultIoc();
    }
}
