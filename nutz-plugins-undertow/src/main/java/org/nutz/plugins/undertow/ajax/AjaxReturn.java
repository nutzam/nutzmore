package org.nutz.plugins.undertow.ajax;

public class AjaxReturn {

    boolean ok;
    String errCode;
    String msg;
    Object data;

    public boolean isOk() {
        return ok;
    }

    public String getErrCode() {
        return errCode;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    public AjaxReturn setOk(boolean ok) {
        this.ok = ok;
        return this;
    }

    public AjaxReturn setErrCode(String errCode) {
        this.errCode = errCode;
        return this;
    }

    public AjaxReturn setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public AjaxReturn setData(Object data) {
        this.data = data;
        return this;
    }

}
