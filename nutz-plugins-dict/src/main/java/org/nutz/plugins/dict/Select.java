package org.nutz.plugins.dict;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 下拉框注解，用于生成全局字典
 * 
 * @author 邓华锋 http://dhf.ink
 * @date 2016年6月29日 上午3:03:43
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Documented
public @interface Select {

	public enum Fields {
		NAME, TEXT, VALUE
	}

	/**
	 * 下拉框ID和name属性的值
	 * 
	 * @author 邓华锋
	 * @date 2016年6月29日 上午11:04:16
	 * 
	 * @return
	 */
	String name() default "";

	/**
	 * 下拉框显示的文本
	 * 
	 * @author 邓华锋
	 * @date 2016年6月29日 上午11:03:50
	 * 
	 * @return
	 */
	Fields text() default Fields.TEXT;

	/**
	 * 下拉框的值
	 * 
	 * @author 邓华锋
	 * @date 2016年6月29日 上午11:04:02
	 * 
	 * @return
	 */
	Fields value() default Fields.VALUE;

}