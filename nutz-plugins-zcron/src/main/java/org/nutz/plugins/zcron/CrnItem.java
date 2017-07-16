package org.nutz.plugins.zcron;

import java.util.List;

/**
 * 表达式的每个项目
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
interface CrnItem {

    /**
     * 判断给定值是否匹配, 给出一个值的范围，以便解析 -1 的值
     * 
     * @param v
     *            被判断的值
     * 
     * @param min
     *            最小值(包括) , -1 表示不限制
     * 
     * @param max
     *            最大值(不包括) , -1 表示不限制
     */
    boolean match(int v, int min, int max);

    /**
     * 将自身的描述，添加到一个列表里
     * 
     * @param ary
     *            要添加的列表
     * @param i18n
     *            字典
     * @param key
     *            自己对应到字典里的键
     */
    void joinText(List<String> ary, ZCroni18n i18n, String key);

}
