package org.nutz.integration.nettice.core.convertor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

/**
 * 定义了基本类型的工具类，
 * 可以方便的判断一个Class对象是否属于基本类型或基本类型的数组。
 * 本工具类所包含的基本类型判断包括如下一些内容：
 * 
 * String
 * boolean
 * byte
 * short
 * int
 * long
 * float
 * double
 * char
 * Boolean
 * Byte
 * Short
 * Integer
 * Long
 * Float
 * Double
 * Character
 * BigInteger
 * BigDecimal
 * 
 * @author yunfeng.cheng
 * @create 2016-08-12
 */
public class PrimitiveType {

	/**
	 * 私有的构造函数防止用户进行实例化。
	 */
	private PrimitiveType() {}

	/** 基本类型  **/
	private static final Class<?>[] PRI_TYPE = { 
			String.class, 
			boolean.class,
			byte.class, 
			short.class, 
			int.class, 
			long.class, 
			float.class,
			double.class, 
			char.class, 
			Boolean.class, 
			Byte.class, 
			Short.class,
			Integer.class, 
			Long.class, 
			Float.class, 
			Double.class,
			Character.class, 
			BigInteger.class, 
			BigDecimal.class 
	};

	/** 基本数组类型  **/
	private static final Class<?>[] PRI_ARRAY_TYPE = { 
			String[].class,
			boolean[].class, 
			byte[].class, 
			short[].class, 
			int[].class,
			long[].class, 
			float[].class, 
			double[].class, 
			char[].class,
			Boolean[].class, 
			Byte[].class, 
			Short[].class, 
			Integer[].class,
			Long[].class, 
			Float[].class, 
			Double[].class, 
			Character[].class,
			BigInteger[].class, 
			BigDecimal[].class 
	};
	
	/**
	 * 基本类型默认值
	 */
	private static final Map<Class<?>, Object> primitiveDefaults = new HashMap<Class<?>, Object>(9);
	static {
        primitiveDefaults.put(boolean.class, false);
        primitiveDefaults.put(byte.class, (byte)0);
        primitiveDefaults.put(short.class, (short)0);
        primitiveDefaults.put(char.class, (char)0);
        primitiveDefaults.put(int.class, 0);
        primitiveDefaults.put(long.class, 0L);
        primitiveDefaults.put(float.class, 0.0f);
        primitiveDefaults.put(double.class, 0.0);
	}
	
	/**
	 * 判断是否为基本类型
	 * @param clasz 需要进行判断的Class对象
	 * @return 是否为基本类型
	 */
	public static boolean isPriType(Class<?> clasz) {
		for (Class<?> priType : PRI_TYPE) {
			if (clasz == priType)
				return true;
		}
		return false;
	}

	/**
	 * 判断是否为基本类型数组
	 * @param clasz 需要进行判断的Class对象
	 * @return 是否为基本类型数组
	 */
	public static boolean isPriArrayType(Class<?> clasz) {
		for (Class<?> priType : PRI_ARRAY_TYPE) {
			if (clasz == priType)
				return true;
		}
		return false;
	}
	
	/**
	 * 获得基本类型的默认值
	 * @param type 基本类型的Class
	 * @return 基本类型的默认值
	 */
	public static Object getPriDefaultValue(Class<?> type) {
		return primitiveDefaults.get(type);
	}
	
}
