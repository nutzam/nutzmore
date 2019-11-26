package org.nutz.plugin.spring.boot.service.entity;

/**
 * @author 王贵源(wangguiyuan@chinarecrm.com.cn)
 *
 *         create at 2019-11-26 10:36:51
 */
public class Pager<T> extends PageredData<T> {

    /**
     * @param page
     * @param pageSize
     */
    public Pager(int page, int pageSize) {
        super(page, pageSize);
    }

    private static final long serialVersionUID = 1L;

}
