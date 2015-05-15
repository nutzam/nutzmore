package org.nutz.plugins.cache.dao.api;

/**
 * 缓存提供者
 * @author wendal(wendal1985@gmail.com)
 *
 */
public interface DaoCacheProvider {

    void init() throws Throwable;
    void depose() throws Throwable;
    
    /**
     * 如果缓存没有找到,返回CacheResult.NOT_FOUNT<p/>
     * 如果缓存的的是空结果(null), 返回CacheResult.NULL
     * 该方法如果返回null,代表缓存实现内部异常!! 
     * @param cacheName
     * @param key
     * @return
     */
    Object get(String cacheName, String key);
    boolean put(String cacheName, String key, Object obj);
    //void update(String cacheName, Object key, Object obj);
    //void remove(String cacheName, Object key);
    //boolean exists(String cacheName, Object key);
    void clear(String cacheName);

}
