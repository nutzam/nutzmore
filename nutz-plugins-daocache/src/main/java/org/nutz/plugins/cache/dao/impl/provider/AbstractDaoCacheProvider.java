package org.nutz.plugins.cache.dao.impl.provider;

import org.nutz.plugins.cache.dao.api.CacheSerializer;
import org.nutz.plugins.cache.dao.api.DaoCacheProvider;

public abstract class AbstractDaoCacheProvider implements DaoCacheProvider {

    protected CacheSerializer serializer;
    
    public void setSerializer(CacheSerializer serializer) {
        this.serializer = serializer;
    }
    
    public CacheSerializer getSerializer() {
        return serializer;
    }

}
