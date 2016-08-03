package net.wendal.nutzbook.service;

public interface DubboWayService {

    String redisSet(String key, String value);
    
    String redisGet(String key);

    String hi(String name);

}