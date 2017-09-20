package org.nutz.integration.nettice.core;

import io.netty.handler.codec.http.HttpRequest;

/**
 * DataHolder 将请求消息对象存放在静态ThreadLocal的实例变量中，保证每个请求的request能通过静态方法获取，从而减少传递参数。
 */
public class DataHolder {
	
	private static final ThreadLocal<Object> localRequset = new ThreadLocal<Object>();
	
	public static void setRequest(HttpRequest request){
		localRequset.set(request);
	}
	
	public static Object getRequest(){
		return localRequset.get();
	}
	
}
