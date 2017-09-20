package org.nutz.integration.nettice.core.convertor;

/**
 * 类型转换器所需要实现的总接口。TypeConverter中有唯一的一个方法,实现类请特别注意方法所需要返回的值。
 * @author yunfeng.cheng
 * @create 2016-08-12
 */
public interface ITypeConverter {

	/**
	 * @param value 需要被转换的值
	 * @param toType 需要被转换成的类型
	 * @param params 转值时需要提供的可选参数
	 * @return 经转换过的类型，如果实现类没有能力进行所指定的类型转换，应返回null
	 */
	public Object convertValue(Object value, Class<?> toType);
	
}
