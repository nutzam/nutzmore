package org.nutz.plugins.cache.dao.impl.provider;

import org.nutz.plugins.cache.dao.api.CacheSerializer;
import org.nutz.plugins.cache.dao.api.DaoCacheProvider;
import org.nutz.plugins.cache.dao.impl.convert.JavaCacheSerializer;

public abstract class AbstractDaoCacheProvider implements DaoCacheProvider {

    /**
     * 序列化器
     */
    protected CacheSerializer serializer;
    
    public void setSerializer(CacheSerializer serializer) {
        this.serializer = serializer;
    }
    
    public CacheSerializer getSerializer() {
        return serializer;
    }

    public void init() throws Throwable {
        if (getSerializer() == null)
            setSerializer(new JavaCacheSerializer());
    }

    public void depose() throws Throwable {}
}
