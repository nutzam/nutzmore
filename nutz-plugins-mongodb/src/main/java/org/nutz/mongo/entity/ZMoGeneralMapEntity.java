package org.nutz.mongo.entity;

import java.util.Map;
import java.util.Set;

import org.nutz.lang.util.NutMap;

import com.mongodb.DBObject;

public class ZMoGeneralMapEntity extends ZMoEntity {

    public ZMoGeneralMapEntity() {
        super();
        setDefaultField(new ZMoGeneralMapField());
        setType(NutMap.class);
        setBorning(this.getMirror().getBorning());
    }

    @Override
    public ZMoEntity forMap() {
        return this;
    }

    @Override
    public ZMoEntity forPojo() {
        return this;
    }

    @Override
    public boolean isForMap() {
        return true;
    }

    @Override
    public boolean isForPojo() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getJavaNames(Object obj) {
        Map<String, Object> map = (Map<String, Object>) obj;
        return map.keySet();
    }

    @Override
    public Set<String> getMongoNames(Object obj) {
        if (obj instanceof DBObject) {
            return ((DBObject) obj).keySet();
        }
        return getJavaNames(obj);
    }

    @Override
    public String getJavaNameFromMongo(String mongoName) {
        return mongoName;
    }

    @Override
    public String getMongoNameFromJava(String javaName) {
        return javaName;
    }

    @Override
    public Object getValue(Object obj, String javaName) {
        return ((Map<?, ?>) obj).get(javaName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object obj, String javaName, Object value) {
        ((Map<String, Object>) obj).put(javaName, value);
    }

    @Override
    public ZMoEntity clone() {
        return new ZMoGeneralMapEntity();
    }

}
