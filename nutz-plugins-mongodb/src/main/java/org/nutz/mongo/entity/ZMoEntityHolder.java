package org.nutz.mongo.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nutz.lang.Strings;

/**
 * 缓存 ZMoEntity 对象
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZMoEntityHolder {

    private Map<String, ZMoEntity> ens;

    public ZMoEntityHolder() {
        ens = new HashMap<String, ZMoEntity>();
    }

    public ZMoEntity get(String key) {
        return ens.get(key);
    }

    public void add(String key, ZMoEntity en) {
        if (null != en && !Strings.isBlank(key)) {
            en.setKey(key);
            ens.put(key, en);
        }
    }

    public Set<String> keys() {
        return ens.keySet();
    }

    public int count() {
        return ens.size();
    }

    public ZMoEntity remove(String key) {
        return ens.remove(key);
    }

    public void clear() {
        ens.clear();
    }

}
