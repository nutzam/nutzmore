package org.nutz.plugins.undertow.query;

import org.nutz.lang.Strings;

public class WebOrderField {

    /**
     * 根据 “ASC:$name” 的格式生成一个排序字段
     * 
     * @param str
     *            排序字段字符串
     * @return 排序字段对象
     */
    public static WebOrderField valueOf(String str) {
        String[] ss = Strings.splitIgnoreBlank(str, ":");
        if (ss == null || ss.length == 0)
            return null;

        WebOrderField wof = new WebOrderField();
        if (ss.length == 1) {
            wof.sort = SORT.DESC;
            wof.name = Strings.trim(ss[0]);
        } else {
            wof.sort = SORT.valueOf(ss[0].toUpperCase());
            wof.name = Strings.trim(ss[1]);
        }

        return wof;
    }

    private SORT sort;

    private String name;

    public SORT getSort() {
        return sort;
    }

    public void setSort(SORT sort) {
        this.sort = sort;
    }

    public boolean asc() {
        return SORT.ASC == sort;
    }

    public boolean desc() {
        return SORT.DESC == sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return String.format("%s:%s", sort, name);
    }
}
