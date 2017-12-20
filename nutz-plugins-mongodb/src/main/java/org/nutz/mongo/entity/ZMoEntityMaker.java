package org.nutz.mongo.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.born.ArrayBorning;
import org.nutz.lang.eject.EjectFromMap;
import org.nutz.lang.inject.InjectToMap;
import org.nutz.mongo.adaptor.ZMoAs;
import org.nutz.mongo.annotation.MoEnum;
import org.nutz.mongo.annotation.MoField;
import org.nutz.mongo.annotation.MoIgnore;

/**
 * 一个 ZMoEntity 字段映射关系的生成器，可以给一个 Map 来自由的描述这个映射关系:<br>
 * Map 的格式定义如下
 * 
 * <pre>
 * {
 *      _class: 'org.nutz.pojo.Pet',   // [选]对象的实现类，如果是 Map 也会指定实现类
 *      
 *      'name': 'nm',     // 下面就是数据库映射，  "Java名" : "数据库字段名"
 *      ...
 * }
 * </pre>
 * <ul>
 * <li>如果一个 map 必选字段是非法的
 * <li>指定了 _class 或者 _type 任何一个，都有办法来确认另外一个值
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZMoEntityMaker {

    /**
     * 根据传入的参考对象，构建一个映射实体。
     * 
     * @param obj
     *            实体参考对象，可以是 POJO, Class, 或者 Map
     * @return 映射实体
     */
    @SuppressWarnings("unchecked")
    public ZMoEntity make(Object obj) {
        Mirror<?> mi = Mirror.me(obj);
        if (mi.isMap()) {
            // 如果传入的就是 Map 的 Class
            if (obj instanceof Class) {
                ZMoGeneralMapEntity en = new ZMoGeneralMapEntity();
                // 如果传入的类不是抽象类，那么必然是一个可实例化的 Map
                if (!Modifier.isAbstract(((Class<?>) obj).getModifiers())) {
                    en.setType(mi.getType());
                    en.setBorning(mi.getBorning());
                }
                return en;
            }

            // 否则传入的一定是个 Map 实例，根据内容来构建实体咯
            return makeMapEntity((Map<String, Object>) obj);
        }
        return makePojoEntity(mi.getType());
    }

    /**
     * 根据一个特殊的 Map 来构建 POJO-ZMoDoc 映射关系
     * 
     * @param pojoType
     *            对象类型
     * @return 映射关系对象
     */
    private ZMoEntity makePojoEntity(Class<?> pojoType) {
        Mirror<?> mi = Mirror.me(pojoType);
        // 保证给的是个 POJO 类型
        if (!mi.isPojo()) {
            throw Lang.makeThrow("!! %s is NOT a kind of POJO", pojoType);
        }
        // 开始创建实体
        ZMoEntity en = new ZMoEntity().forPojo();
        en.setType(pojoType);
        en.setBorning(mi.getBorning());

        // 构建映射字段
        for (Field fld : mi.getFields()) {
            // 临时字段忽略
            if (Modifier.isTransient(fld.getModifiers()))
                continue;
            // 特殊指明要忽略的字段也要忽略
            if (null != fld.getAnnotation(MoIgnore.class))
                continue;

            // 创建实体
            ZMoField mof = new ZMoField();

            // 设置映射关系
            mof.setJavaName(fld.getName());
            MoField anFld = fld.getAnnotation(MoField.class);
            if (null == anFld || Strings.isBlank(anFld.value())) {
                mof.setMongoName(fld.getName());
            } else {
                mof.setMongoName(anFld.value());
            }

            // 对枚举的特殊配置
            MoEnum anEnum = fld.getAnnotation(MoEnum.class);
            if (null != anEnum) {
                mof.setEnumStr(anEnum.str());
            }
            // 没声明，默认用 string 表示 ENUM
            else {
                mof.setEnumStr(true);
            }

            // 处理字段类型相关的适配方法
            mof.setType(fld.getType());
            Mirror<?> fmi = Mirror.me(fld.getType());
            mof.setAdaptor(ZMoAs.get(fmi));
            mof.setEjecting(mi.getEjecting(fld));
            mof.setInjecting(mi.getInjecting(fld.getName()));

            /*
             * 处理字段对象的生成方式，只有容器或者数组比较特殊
             */
            // Collection
            if (fmi.isCollection()) {
                // 获得元素类型
                mof.setEleType(Mirror.getGenericTypes(fld, 0));
                if (null == mof.getEleType()) {
                    throw Lang.makeThrow("can not fould eleType for fld %s of %s",
                                         fld.getName(),
                                         mi.getType());
                }

                // 如果不是抽象的，那么就用这个类型来生成对象实例
                if (!Modifier.isAbstract(fmi.getType().getModifiers())) {
                    mof.setBorning(fmi.getBorning());
                }
                // 抽象类型 Collection
                // 抽象类型 List
                // 抽象类型 Queue
                else if (fmi.is(Collection.class) || fmi.is(List.class) || fmi.is(Queue.class)) {
                    mof.setBorning(Mirror.me(LinkedList.class).getBorning());
                }
                // 抽象类型 Set
                else if (fmi.is(Set.class)) {
                    mof.setBorning(Mirror.me(LinkedHashSet.class).getBorning());
                }
                // 其他类型，抛错
                else {
                    throw Lang.makeThrow("can not found borning for %s", fmi.getType());
                }

            }
            // 数组
            else if (fmi.isArray()) {
                // 获得元素类型
                mof.setEleType(fld.getType().getComponentType());
                // 为数组创建一个生成方式
                mof.setBorning(new ArrayBorning(mof.getEleType()));
            }

            // 加入到实体中
            en.addField(mof);
        }

        // 返回
        return en;
    }

    /**
     * 根据一个特殊的 Map 来构建 Map-ZMoDoc 映射关系
     * 
     * @param map
     *            一个描述映射关系的 Map 对象
     * @return 映射关系对象
     */
    public ZMoEntity makeMapEntity(Map<String, Object> map) {
        Mirror<?> mi = null;
        // 看看是不是指定了实体的特殊实现类名称
        if (map.containsKey("_class")) {
            try {
                mi = Mirror.me(Class.forName(map.get("_class").toString()));
            }
            catch (ClassNotFoundException e) {
                throw Lang.wrapThrow(e);
            }
        }

        // 准备创建实体
        ZMoEntity en = null;

        /*
         * 如果指定的实现类是个 POJO，那么就先根据类定义构建它
         */
        if (null != mi && mi.isPojo()) {
            en = makePojoEntity(mi.getType());
        }
        // 如果不是 POJO 那就是 Map 咯，创建一个 Map 实体
        else {
            en = new ZMoEntity().forMap();
            // 设置实体的 Java 实现类类型
            if (null == mi)
                en.setType(HashMap.class);
            else
                en.setType(mi.getType());
            // 设置实体需要其他字段
            en.setBorning(en.getMirror().getBorning());

            en.setDefaultField(new ZMoGeneralMapField());
        }

        /*
         * 处理纯 Map 的映射
         */
        for (String key : map.keySet()) {
            // 忽略空值
            String val = Strings.sNull(map.get(key), null);
            if (null == val)
                continue;
            // 忽略隐藏属性
            if (key.startsWith("_"))
                continue;
            // 逐个处理每个字段
            ZMoField fld = new ZMoField();
            fld.setType(Object.class);
            fld.setJavaName(key);
            fld.setMongoName(val);
            fld.setAdaptor(ZMoAs.smart());
            fld.setBorning(Mirror.me(HashMap.class).getBorning());
            fld.setEjecting(new EjectFromMap(key));
            fld.setInjecting(new InjectToMap(key));
            en.addField(fld);
        }
        return en;
    }

}
