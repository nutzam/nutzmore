package org.nutz.plugins.validation;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.el.El;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;

/**
 * 辅助验证的工具类
 * <p>
 * 该类为虚类，不能实例化，只能以静态的方式调用内部方法
 * 
 * @author QinerG(QinerG@gmail.com)
 */
public abstract class ValidationUtils {

	private static final String mobile_regex = "1\\d{10}";
	private static final String email_regex = "^([\\w-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([\\w-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	private static final String chinese_regex = "[\u4e00-\u9fa5]+";
	private static final String qq_regex = "[1-9][0-9]{4,}";
	private static final String post_regex = "[1-9]\\d{5}(?!\\d)";
	private static final String account_regex = "^[a-zA-Z][a-zA-Z0-9_]+";

	/**
	 * 必填字段验证
	 * 
	 * @param fieldName
	 *            待验证字段名
	 * @param obj
	 *            待验证对象
	 * @param errorMsg
	 *            验证错误后的提示语
	 * @param errors
	 *            存储错误信息的对象
	 * @return 返回是否通过验证
	 */
	public static boolean required(String fieldName, Object target, String errorMsg, Errors errors) {
		if (null == target) {
			errors.add(fieldName, errorMsg);
			return false;
		}
		if (target instanceof String && Strings.isBlank((String) target)) {
			errors.add(fieldName, errorMsg);
			return false;
		}
		return true;
	}

	/**
	 * 正则表达式验证
	 * 
	 * @param fieldName
	 * @param obj
	 * @param regex
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean regex(String fieldName, Object target, String regex, String errorMsg, Errors errors) {
		if (null == target || !(target instanceof String)) {
			errors.add(fieldName, errorMsg);
			return false;
		}

		final Matcher m = Pattern.compile(regex, Pattern.MULTILINE + Pattern.DOTALL).matcher((String) target);
		if (!m.matches()) {
			errors.add(fieldName, errorMsg);
			return false;
		}
		return true;
	}

	/**
	 * 手机号验证
	 * 
	 * @param fieldName
	 * @param obj
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean mobile(String fieldName, Object target, String errorMsg, Errors errors) {
		return regex(fieldName, target, mobile_regex, errorMsg, errors);
	}

	/**
	 * 国内邮政编码验证
	 * 
	 * @param fieldName
	 * @param target
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean post(String fieldName, Object target, String errorMsg, Errors errors) {
		return regex(fieldName, target, post_regex, errorMsg, errors);
	}

	/**
	 * Email 验证
	 * 
	 * @param fieldName
	 * @param obj
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean email(String fieldName, Object target, String errorMsg, Errors errors) {
		return regex(fieldName, target, email_regex, errorMsg, errors);
	}

	/**
	 * 只允许中文验证
	 * 
	 * @param fieldName
	 * @param obj
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean chinese(String fieldName, Object target, String errorMsg, Errors errors) {
		return regex(fieldName, target, chinese_regex, errorMsg, errors);
	}

	/**
	 * QQ号 验证
	 * 
	 * @param fieldName
	 * @param obj
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean qq(String fieldName, Object target, String errorMsg, Errors errors) {
		return regex(fieldName, target, qq_regex, errorMsg, errors);
	}

	/**
	 * 账号验证
	 * 
	 * @param fieldName
	 * @param target
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean account(String fieldName, Object target, String errorMsg, Errors errors) {
		return regex(fieldName, target, account_regex, errorMsg, errors);
	}

	/**
	 * 重复性验证。两个字段的值必须一致，允许空值。
	 * 
	 * @param fieldName
	 * @param target
	 * @param repeatValue
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean repeat(	String fieldName,
									Object target,
									Object repeatValue,
									String errorMsg,
									Errors errors) {
		if (!(null == target) && !target.equals(repeatValue)) {
			errors.add(fieldName, errorMsg);
			return false;
		}
		if (!(null == repeatValue) && !repeatValue.equals(target)) {
			errors.add(fieldName, errorMsg);
			return false;
		}
		return true;
	}

	/**
	 * 字符串长度必须在一定区间范围内验证
	 * 
	 * @param fieldName
	 * @param obj
	 * @param interval
	 *            长度限定区间，如果传空的数组也不会报错，但验证将总是会通过
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean stringLength(	String fieldName,
										Object target,
										int[] interval,
										String errorMsg,
										Errors errors) {
		int minLength = 0;
		int maxLength = Integer.MAX_VALUE;
		if (interval.length >= 1) {
			minLength = interval[0];
		}
		if (interval.length >= 2) {
			maxLength = interval[1];
		}
		return stringLength(fieldName, target, minLength, maxLength, errorMsg, errors);
	}

	/**
	 * 字符串长度必须在一定区间范围内验证
	 * 
	 * @param fieldName
	 * @param obj
	 * @param minLength
	 * @param maxLength
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean stringLength(	String fieldName,
										Object target,
										int minLength,
										int maxLength,
										String errorMsg,
										Errors errors) {
		if (null == target || !(target instanceof String)) {
			errors.add(fieldName, errorMsg);
			return false;
		}
		String str = (String) target;
		if (str.length() < minLength || str.length() > maxLength) {
			errors.add(fieldName, errorMsg);
			return false;
		}
		return true;
	}

	/**
	 * 判断指定值是否在某个区间
	 * 
	 * @param fieldName
	 * @param target
	 * @param interval
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean limit(String fieldName, Object target,	double[] interval,	String errorMsg, Errors errors) {
		double minLength = 0;
		double maxLength = Double.MAX_VALUE;
		if (interval.length >= 1) {
			minLength = interval[0];
		}
		if (interval.length >= 2) {
			maxLength = interval[1];
		}
		return limit(fieldName, target, minLength, maxLength, errorMsg, errors);
	}

	/**
	 * 判断指定值是否在某个区间,兼容 int、long、float、double
	 * 
	 * @param fieldName
	 * @param target
	 * @param interval
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean limit(String fieldName, Object target,	double minValue, double maxValue, String errorMsg, Errors errors) {
		if (null == target) {
			errors.add(fieldName, errorMsg);
			return false;
		}
		Double d = null;
		if (target instanceof Double) {
			d = (Double) target;
		} else if (target instanceof Integer) {
			d = ((Integer) target).doubleValue();
		} else if (target instanceof Long) {
			d = ((Long) target).doubleValue();
		} else if (target instanceof Float) {
			d = ((Float) target).doubleValue();
		}
		if (d == null || d < minValue || d > maxValue) {
			errors.add(fieldName, errorMsg);
			return false;
		}
		return true;
	}

	/**
	 * 通过 nutz 自带的 el 表达式进行验证
	 * @param fieldName
	 * @param obj
	 * @param el 表达式
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean el(String fieldName, Object obj, String el, String errorMsg, Errors errors) {
		Context context = Lang.context();
		context.set("value", obj);
		Object val = El.eval(context, el);
		if (! (val instanceof Boolean)) {
			errors.add(fieldName, errorMsg);
			return false;
		}
		return true;
	}

	/**
	 * 自定义验证方法
	 * 
	 * @param fieldName
	 * @param obj
	 * @param customFunction
	 *            自定义验证方法名称，注意该方法必须在 obj 里用 public 声明，且返回值为 boolean 型，否则会抛出异常
	 * @param errorMsg
	 * @param errors
	 * @return
	 */
	public static boolean custom(	String fieldName, Object obj, String customFunction, String errorMsg, Errors errors) {
		Mirror<?> mirror = Mirror.me(obj.getClass());
		Method[] mds = mirror.getMethods();
		boolean find = false;
		for (Method md : mds) {
			if (md.getName().equals(customFunction)) {
				find = true;
				try {
					boolean ret = (Boolean) md.invoke(obj);
					if (!ret) {
						errors.add(fieldName, errorMsg);
						return false;
					}
					return true;
				}
				catch (Exception e) {
					errors.add(fieldName, errorMsg);
					e.printStackTrace();
					return false;
				}
			}
		}

		if (!find) {
			// 没有找到指定的方法
			errors.add(fieldName, errorMsg);
			return false;
		}
		return true;
	}

	/**
	 * 检查方法的参数中是否存在 Errors 的对象，没有则返回空
	 * 
	 * @param argsClass
	 * @param args
	 * @return
	 */
	public static Errors checkArgs(Class<?>[] argsClass, Object... args) {
		Errors es = null;
		for (int i = 0; i < argsClass.length; i++) {
			if (argsClass[i] == Errors.class) {
				if (args[i] == null) {
					args[i] = es = new Errors();
				} else {
					es = (Errors) args[i];
				}
				break;
			}
		}

		return es;
	}

}
