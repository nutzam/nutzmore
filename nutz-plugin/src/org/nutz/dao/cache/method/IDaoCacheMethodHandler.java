/**
 * 
 */
package org.nutz.dao.cache.method;

import org.nutz.dao.cache.ObsArgClass;

/**
 * @author liaohongliu
 *
 * 创建时间: 2011-4-21
 */
public interface IDaoCacheMethodHandler {
	/**
	 * 相关方法的缓存处理方法
	 * @param msg 封装的对象,具体看这个类中注释
	 * @return 调用原方法或获取缓存中值后返回的值
	 */
	public Object handler(ObsArgClass msg);
}
