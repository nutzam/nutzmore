package org.nutz.plugins.cache.dao.impl.convert;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nustaq.serialization.FSTConfiguration;
import org.nutz.plugins.cache.dao.CacheResult;
import org.nutz.resource.Scans;

public class FstSerializer extends AbstractCacheSerializer {
    
    protected FSTConfiguration fstConf;
    
    public FstSerializer() {
        fstConf = FSTConfiguration.createDefaultConfiguration();
    }
    
    public FstSerializer(FSTConfiguration fstConf) {
        this.fstConf = fstConf;
    }

    public Object from(Object obj) {
        if (obj == null)
            return null;
        return fstConf.asByteArray(obj);
    }

    public Object back(Object obj) {
        if (isNULL_OBJ(obj))
            return CacheResult.NULL;
        return fstConf.asObject((byte[])obj);
    }

    public void setBeanPackages(String ... packages) {
        List<Class<?>> list = new ArrayList<>();
        for (String pkg : packages) {
            for (Class<?> klass : Scans.me().scanPackage(pkg)) {
                if (Serializable.class.isAssignableFrom(klass))
                    list.add(klass);
            }
        }
        if (list.isEmpty())
            return;
        fstConf.registerClass(list.toArray(new Class<?>[list.size()]));
    }
}
