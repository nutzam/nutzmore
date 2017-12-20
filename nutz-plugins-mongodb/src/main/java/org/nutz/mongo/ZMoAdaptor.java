package org.nutz.mongo;

import org.nutz.mongo.entity.ZMoField;

/**
 * 将 Mongo 驱动的字段值与普通 Java 字段值互相转换的适配器
 * <p>
 * 每个适配器的实例将只能处理特定范围的数据类型，比如 ZMoMapAdaptor 只能处理 Map 
 * <p>
 * 注意:
 * <ol>
 * <li>所有的适配器都不会处理 null 这个情况
 * </ol>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ZMoAdaptor {

    /**
     * 将任何 Mongo 驱动的数据类型变成 Java 的值
     * 
     * @param fld
     *            要映射的字段
     * @param obj
     *            字段值
     * 
     * @return 适合普通 Java 程序的字段值
     */
    Object toJava(ZMoField fld, Object obj);

    /**
     * 将任何 Java 字段值变成 Mongo 驱动能接受的值
     * 
     * @param fld
     *            要映射的字段
     * @param obj
     *            字段值
     * 
     * @return Mongo 驱动能接受的值
     */
    Object toMongo(ZMoField fld, Object obj);
}
