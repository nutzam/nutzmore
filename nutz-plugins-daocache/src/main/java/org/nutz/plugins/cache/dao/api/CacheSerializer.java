package org.nutz.plugins.cache.dao.api;

/**
 * 缓存序列化器, 实现类必须是线程安全的
 * @author wendal(wendal1985@gmail.com)
 *
 */
public interface CacheSerializer {
    
    Object from(Object obj);
    
    Object back(Object obj);

}
