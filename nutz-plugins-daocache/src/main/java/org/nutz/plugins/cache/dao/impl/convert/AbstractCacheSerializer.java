package org.nutz.plugins.cache.dao.impl.convert;

import org.nutz.plugins.cache.dao.api.CacheSerializer;

public abstract class AbstractCacheSerializer implements CacheSerializer {

    public static final byte[] NULL_OBJ = new byte[0];

    protected boolean isNULL_OBJ(Object obj) {
        if (obj == null)
            return true;
        if (!(obj instanceof byte[]))
            throw new IllegalArgumentException("Not byte[] --> " + obj.getClass());
        byte[] data = (byte[])obj;
        if (data.length == 0)
            return true;
        return false;
    }
}
