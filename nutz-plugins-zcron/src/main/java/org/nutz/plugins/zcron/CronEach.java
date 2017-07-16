package org.nutz.plugins.zcron;

/**
 * 迭代回调接口
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * @param <T>
 *            被填充的数组元素类型
 */
public interface CronEach<T> {

	/**
	 * 迭代回调函数
	 * 
	 * @param array
	 *            被填充的数组
	 * @param index
	 *            当前匹配的下标
	 */
	void invoke(T[] array, int index) throws Exception;

}
