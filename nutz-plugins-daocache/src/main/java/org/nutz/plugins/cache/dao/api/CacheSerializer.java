package org.nutz.plugins.cache.dao.api;

public interface CacheSerializer {
    
    Object from(Object obj);
    
    Object back(Object obj);

}
