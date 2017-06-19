package org.nutz.plugins.undertow.ajax;

import org.nutz.lang.util.NutMap;

public abstract class Ajax {

    public static AjaxReturn ok() {
        AjaxReturn re = new AjaxReturn();
        re.ok = true;
        return re;
    }

    public static AjaxReturn fail() {
        AjaxReturn re = new AjaxReturn();
        re.ok = false;
        return re;
    }

    public static AjaxReturn expired() {
        AjaxReturn re = new AjaxReturn();
        re.ok = false;
        re.msg = "ajax.expired";
        return re;
    }

    /**
     * @return 获得一个map，用来存放返回的结果。
     */
    public static NutMap one() {
        return new NutMap();
    }

}
