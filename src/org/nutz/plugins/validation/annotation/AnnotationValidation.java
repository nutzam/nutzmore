package org.nutz.plugins.validation.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.plugins.validation.Errors;
import org.nutz.plugins.validation.Validation;
import org.nutz.plugins.validation.ValidationUtils;

/**
 * 使用注解驱动的验证解决方案
 * 
 * @see ValidationUtils
 * @see Validation
 * @author QinerG(QinerG@gmail.com)
 */
public class AnnotationValidation implements Validation {

	/**
	 * 通过注解对一个Pojo进行验证
	 */
	public Errors validate(Object target) {
		Errors errors = new Errors();
		validate(target, errors);
		return errors;
	}

	/**
	 * 通过注解对一个Pojo进行验证
	 */
	public void validate(Object target, Errors errors) {
		if (null == target) {
			return;
		}
		// 遍历对象的所有字段
		Mirror<?> mirror = Mirror.me(target.getClass());
		Field[] fields = mirror.getFields(Validations.class);
		for (Field field : fields) {
			// 检查该字段是否声明了需要验证
			Validations vals = field.getAnnotation(Validations.class);
			String errMsg = vals.errorMsg();
			try {
				Method getMethod = mirror.getGetter(field);

				Object value = getMethod.invoke(target, new Object[]{}); // 这个对象字段get方法的值

				// 验证该字段是否必须
				if (vals.required() && !ValidationUtils.required(field.getName(), value, errMsg, errors)) {
					continue;
				}

				// 账号验证
				if (vals.account() && !ValidationUtils.account(field.getName(), value, errMsg, errors)) {
					continue;
				}

				// 手机号码验证
				if (vals.mobile() && !ValidationUtils.mobile(field.getName(), value, errMsg, errors)) {
					continue;
				}

				// 验证是否为 email
				if (vals.email() && !ValidationUtils.email(field.getName(), value, errMsg, errors)) {
					continue;
				}

				// 验证是否为 qq 号
				if (vals.qq() && !ValidationUtils.qq(field.getName(), value, errMsg, errors)) {
					continue;
				}

				// 必须为中文效验
				if (vals.chinese() && !ValidationUtils.chinese(field.getName(), value, errMsg, errors)) {
					continue;
				}

				// 邮政编码效验
				if (vals.post() && !ValidationUtils.post(field.getName(), value, errMsg, errors)) {
					continue;
				}

				// 验证正则表达式
				if (!Strings.isBlank(vals.regex())
					&& !ValidationUtils.regex(field.getName(), value, vals.regex(), errMsg, errors)) {
					continue;
				}

				// 验证该字段长度
				if (vals.strLen().length > 0
					&& !ValidationUtils.stringLength(field.getName(), value, vals.strLen(), errMsg, errors)) {
					continue;
				}

				// 重复值检验
				if (!Strings.isBlank(vals.repeat())) {
					Object repeatValue = mirror.getGetter(vals.repeat()).invoke(target, new Object[]{});
					if (!ValidationUtils.repeat(field.getName(), value, repeatValue, errMsg, errors)) {
						continue;
					}
				}

				// 判断指定值是否在某个区间
				if (vals.limit().length > 0
					&& !ValidationUtils.limit(field.getName(), value, vals.limit(), errMsg, errors)) {
					continue;
				}
				
				//通过 el 表达式进行数值验证
				if (!Strings.isBlank(vals.el()) && !ValidationUtils.el(field.getName(), value, vals.el(), errMsg, errors)) {
					continue;
					
				}

				// 自定义验证方法
				if (!Strings.isBlank(vals.custom())
					&& !ValidationUtils.custom(field.getName(), target, vals.custom(), errMsg, errors)) {
					continue;
				}

			}
			catch (Exception e) {}
		}
	}
}
