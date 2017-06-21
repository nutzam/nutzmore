package org.nutz.plugins.undertow.ajax;

/**
 * Ajax 消息体封装
 */
public class AjaxT <T> {

	boolean ok;
	String msg;
	T data;

	public boolean isOk() {
		return ok;
	}

	public String getMsg() {
		return msg;
	}

	public T getData() {
		return data;
	}

	public AjaxT<T> setOk(boolean ok) {
		this.ok = ok;
		return this;
	}

	public AjaxT<T> setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public AjaxT<T> setData(T data) {
		this.data = data;
		return this;
	}
	
	public static final <T> AjaxT<T> Create(Class<T> klass) {
		return new AjaxT<T>();
	}
}
