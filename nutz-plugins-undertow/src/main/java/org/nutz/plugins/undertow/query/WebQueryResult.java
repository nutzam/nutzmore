package org.nutz.plugins.undertow.query;

import java.util.LinkedList;

/**
 * 描述了一个分页查询结果
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class WebQueryResult<T> {

    /**
     * 当前第几页
     */
    public int pn;

    /**
     * 一页最多有多少记录
     */
    public int pgsz;

    /**
     * 【选】 一共多少页
     */
    public int pgcount;

    /**
     * 【选】一共有多少记录
     */
    public int rcount;

    /**
     * 【选】是否还有更多的记录
     */
    public boolean hasMore;

    public LinkedList<T> list;

    public WebQueryResult() {
        list = new LinkedList<T>();
    }

    public WebQueryResult(WebQuery q) {
        this();
        pn = q.getPageNumber();
        pgsz = q.getPageSize();
    }

    public WebQueryResult<T> add(T o) {
        list.add(o);
        return this;
    }
}
