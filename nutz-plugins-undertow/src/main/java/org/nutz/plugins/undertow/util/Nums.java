package org.nutz.plugins.undertow.util;

import org.nutz.lang.Lang;

/**
 * 将http接收到string参数安全地转换为常用数据格式
 * 
 * @author qinerg@gmail.com
 * @version 2012-5-25 上午10:57:23
 */
public class Nums {

	public static int toInt(String value) {
		return toInt(value, 0);
	}

	/**
	 * 安全地将 str 转换为 int，转换失败时会返回缺省值
	 */
	public static int toInt(String value, int defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static long toLong(String value) {
		return toLong(value, 0L);
	}

	public static long toLong(String value, long defaultValue) {
		try {
			return Long.parseLong(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static double toDouble(String value) {
		return toDouble(value, 0L);
	}

	public static double toDouble(String value, double defaultValue) {
		try {
			return Double.parseDouble(value);
		} catch (Exception e) {
			return defaultValue;
		}
	}

	public static boolean toBool(String value) {
		return Lang.parseBoolean(value);
	}

}
