package org.nutz.mongo.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.born.Borning;
import org.nutz.lang.eject.Ejecting;

/**
 * 描述了一个对象字段到 Mongo Document 之间的字段映射关系
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZMoEntity {

    enum Mode {
        MAP, POJO
    }

    /**
     * 在 holder 中保存的唯一键值
     */
    private String key;

    /**
     * 指定了这个映射是 Map 还是 Pojo
     */
    private Mode mode;

    /**
     * 对应的 Java 类型，如果是 Map 则表示 Map 的实现类
     */
    private Class<?> type;

    private Mirror<?> mirror;

    /**
     * 保存了对象的实例生成方法，以便能较快速的生成对象
     */
    private Borning<?> borning;

    /**
     * 以 java 字段名为索引的映射字段表
     */
    private Map<String, ZMoField> byJava;

    /**
     * 以 mongoDB 字段名为索引的映射字段表
     */
    private Map<String, ZMoField> byMongo;

    /**
     * 默认的字段映射方法，这个字段，并不会在 getXXXXNames() 时出现
     */
    private ZMoField defaultField;

    public ZMoEntity() {
        byJava = new HashMap<String, ZMoField>();
        byMongo = new HashMap<String, ZMoField>();
        defaultField = null;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDefaultField(ZMoField defaultField) {
        this.defaultField = defaultField;
    }

    public ZMoEntity forMap() {
        mode = Mode.MAP;
        return this;
    }

    public ZMoEntity forPojo() {
        mode = Mode.POJO;
        return this;
    }

    public boolean isForMap() {
        return Mode.MAP == mode;
    }

    public boolean isForPojo() {
        return Mode.POJO == mode;
    }

    public Class<?> getType() {
        return type;
    }

    public ZMoEntity setType(Class<?> type) {
        this.type = type;
        this.mirror = Mirror.me(type);
        return this;
    }

    public Mirror<?> getMirror() {
        return mirror;
    }

    public Object born(Object... args) {
        return borning.born(args);
    }

    public ZMoEntity setBorning(Borning<?> borning) {
        this.borning = borning;
        return this;
    }

    public void addField(ZMoField fld) {
        fld.setParent(this);
        if (!Strings.isBlank(fld.getJavaName()))
            byJava.put(fld.getJavaName(), fld);
        if (!Strings.isBlank(fld.getMongoName()))
            byMongo.put(fld.getMongoName(), fld);
    }

    public Set<String> getJavaNames(Object obj) {
        return byJava.keySet();
    }

    public Set<String> getMongoNames(Object obj) {
        return byMongo.keySet();
    }

    public String getJavaNameFromMongo(String mongoName) {
        return mongoField(mongoName).getJavaName();
    }

    public String getMongoNameFromJava(String javaName) {
        return javaField(javaName).getMongoName();
    }

    /**
     * 获取实体字段
     * 
     * @param name
     *            字段名
     * @return 实体字段，如果没找到回 defaultField
     */
    public ZMoField getJavaField(String name) {
        ZMoField fld = byJava.get(name);
        return fld == null ? defaultField : fld;
    }

    /**
     * 获取实体字段
     * 
     * @param name
     *            字段名
     * @return 实体字段，如果是 null 则抛错
     * @see #getField(String)
     */
    public ZMoField javaField(String name) {
        ZMoField fld = getJavaField(name);
        if (null == fld) {
            throw Lang.makeThrow("no such field! %s->%s", type, name);
        }
        return fld;
    }

    /**
     * 获取实体字段
     * 
     * @param name
     *            字段名
     * @return 实体字段，如果没找到回 defaultField
     */
    public ZMoField getMongoField(String name) {
        ZMoField fld = byMongo.get(name);
        return fld == null ? defaultField : fld;
    }

    /**
     * 获取实体字段
     * 
     * @param name
     *            字段名
     * @return 实体字段，如果是 null 则抛错
     * @see #getField(String)
     */
    public ZMoField mongoField(String name) {
        ZMoField fld = getMongoField(name);
        if (null == fld) {
            throw Lang.makeThrow("no such field! %s->%s", type, name);
        }
        return fld;
    }

    /**
     * 获取对象某一特殊字段名称
     * 
     * @param obj
     *            对象
     * @param javaName
     *            字段名
     * @return 字段值
     */
    public Object getValue(Object obj, String javaName) {
        ZMoField fld = javaField(javaName);
        Ejecting ejecting = fld.getEjecting();
        return ejecting.eject(obj);
    }

    /**
     * 为对象某特殊字段设置值
     * 
     * @param obj
     *            对象
     * @param javaName
     *            字段名
     * @param value
     *            字段值
     */
    public void setValue(Object obj, String javaName, Object value) {
        javaField(javaName).getInjecting().inject(obj, value);
    }

    public ZMoEntity clone() {
        ZMoEntity en = new ZMoEntity();
        en.setType(type);
        en.setBorning(borning);
        en.mode = this.mode;
        for (Map.Entry<String, ZMoField> fld : byJava.entrySet()) {
            ZMoField f2 = fld.getValue().clone();
            f2.setParent(en);
            en.byJava.put(fld.getKey(), f2);
        }
        for (Map.Entry<String, ZMoField> fld : byMongo.entrySet()) {
            ZMoField f2 = fld.getValue().clone();
            f2.setParent(en);
            en.byMongo.put(fld.getKey(), f2);
        }
        return en;
    }

}
