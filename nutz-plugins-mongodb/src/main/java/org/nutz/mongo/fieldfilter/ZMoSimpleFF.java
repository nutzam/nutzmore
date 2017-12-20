package org.nutz.mongo.fieldfilter;

import java.util.List;

import org.nutz.lang.Lang;

/**
 * 根据给定的 java 字段名字，来判断是否忽略该字段
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZMoSimpleFF extends ZMoFF {

    private String[] names;

    /**
     * @param names
     *            给定一个字段名称列表（大小写敏感）
     */
    public ZMoSimpleFF(String... names) {
        super();
        this.names = names;
    }

    public ZMoSimpleFF(List<String> names) {
        this.names = names.toArray(new String[names.size()]);
    }

    @Override
    public boolean match(String fld) {
        return Lang.contains(names, fld);
    }

}
