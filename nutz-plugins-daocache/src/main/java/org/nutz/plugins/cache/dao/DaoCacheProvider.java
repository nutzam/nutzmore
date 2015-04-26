package org.nutz.plugins.cache.dao;

public interface DaoCacheProvider {

    void init() throws Throwable;
    void depose() throws Throwable;
    
    Object get(String cacheName, String key);
    boolean put(String cacheName, String key, Object obj);
    //void update(String cacheName, Object key, Object obj);
    //void remove(String cacheName, Object key);
    //boolean exists(String cacheName, Object key);
    void clear(String cacheName);

}
