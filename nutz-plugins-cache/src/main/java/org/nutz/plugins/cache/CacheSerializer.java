package org.nutz.plugins.cache;

/**
 * 缓存序列化器, 实现类必须是线程安全的
 * @author wendal(wendal1985@gmail.com)
 *
 */
public interface CacheSerializer {
    
    /**
     * 如果对象无法序列化,返回null
     */
    Object fromObject(Object obj);
    
    /**
     * 要求: 如果对象无法还原,返回null
     */
    Object toObject(Object obj);

}
