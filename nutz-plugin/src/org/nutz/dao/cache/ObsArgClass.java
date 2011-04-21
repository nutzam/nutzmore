/**
 * 
 */
package org.nutz.dao.cache;

import java.lang.reflect.Method;

/**
 * @author liaohongliu
 *
 * 创建时间: 2011-4-21
 */
public class ObsArgClass {
	private Method method;
	private Object[] args;
	private CacheStrategy cacheStrategy;
	private Cache cache;
	
	public ObsArgClass(Method method, Object[] args,CacheStrategy cacheUtis,Cache cache) {
		super();
		this.method = method;
		this.args = args;
		this.cacheStrategy=cacheUtis;
		this.cache=cache;
	}
	
	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object[] args) {
		this.args = args;
	}

	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	public String getMethodName(){
		return method.getName();
	}

	public CacheStrategy getCacheStrategy() {
		return cacheStrategy;
	}

	public void setCacheStrategy(CacheStrategy cacheStrategy) {
		this.cacheStrategy = cacheStrategy;
	}

	public Cache getCache() {
		return cache;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}
}
