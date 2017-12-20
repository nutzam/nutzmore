package org.nutz.mongo.entity;

import org.nutz.lang.Mirror;
import org.nutz.lang.born.Borning;
import org.nutz.lang.eject.Ejecting;
import org.nutz.lang.inject.Injecting;
import org.nutz.mongo.ZMoAdaptor;
import org.nutz.mongo.adaptor.ZMoAs;

/**
 * 封装了一个 Java 对象字段映射以及存取值操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZMoField {

    public ZMoField() {}

    private String javaName;

    private String mongoName;

    /**
     * 字段的类型
     */
    private Class<?> type;

    private Mirror<?> mirror;

    /**
     * 元素类型，仅为数组或者容器使用
     */
    private Class<?> eleType;

    private Mirror<?> eleMirror;

    /**
     * 元素适配器，仅为数组或者容器使用
     */
    private ZMoAdaptor eleAdaptor;

    /**
     * 字段值的创建方式，通常是针对容器类或者POJO类
     */
    private Borning<?> borning;

    /**
     * 字段所属的映射实体
     */
    private ZMoEntity parent;

    /**
     * 对于字段值的处理方式
     */
    private ZMoAdaptor adaptor;

    /**
     * 从字段中取出值的方法
     */
    private Ejecting ejecting;

    /**
     * 向字段赋值的方法
     */
    private Injecting injecting;

    /**
     * 仅对枚举字段有效，指明枚举字段是要保存成整数还是字符串
     */
    private boolean enum_is_str;

    public boolean isID() {
        return "_id".equals(javaName);
    }

    public String getJavaName() {
        return javaName;
    }

    public void setJavaName(String javaName) {
        this.javaName = javaName;
    }

    public String getMongoName() {
        return mongoName;
    }

    public void setMongoName(String mongoName) {
        this.mongoName = mongoName;
    }

    public boolean isEnumStr() {
        return enum_is_str;
    }

    public void setEnumStr(boolean isEnumString) {
        this.enum_is_str = isEnumString;
    }

    public Borning<?> getBorning() {
        return borning;
    }

    public void setBorning(Borning<?> borning) {
        this.borning = borning;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
        this.mirror = Mirror.me(type);
    }

    public Mirror<?> getMirror() {
        return mirror;
    }

    public Class<?> getEleType() {
        return eleType;
    }

    public void setEleType(Class<?> eleType) {
        this.eleType = eleType;
        this.eleMirror = Mirror.me(eleType);
        this.eleAdaptor = ZMoAs.get(eleMirror);
    }

    public Mirror<?> getEleMirror() {
        return eleMirror;
    }

    public ZMoAdaptor getEleAdaptor() {
        return eleAdaptor;
    }

    public void setEleAdaptor(ZMoAdaptor eleAdaptor) {
        this.eleAdaptor = eleAdaptor;
    }

    public ZMoEntity getParent() {
        return parent;
    }

    public void setParent(ZMoEntity parent) {
        this.parent = parent;
    }

    public ZMoAdaptor getAdaptor() {
        return adaptor;
    }

    public void setAdaptor(ZMoAdaptor adaptor) {
        this.adaptor = adaptor;
    }

    public Ejecting getEjecting() {
        return ejecting;
    }

    public void setEjecting(Ejecting ejecting) {
        this.ejecting = ejecting;
    }

    public Injecting getInjecting() {
        return injecting;
    }

    public void setInjecting(Injecting injecting) {
        this.injecting = injecting;
    }

    public ZMoField clone() {
        ZMoField fld = new ZMoField();
        fld.setEjecting(ejecting);
        fld.setInjecting(injecting);
        fld.setBorning(borning);
        fld.setEnumStr(enum_is_str);
        fld.setEleType(eleType);
        return fld;
    }

}
