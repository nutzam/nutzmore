package org.nutz.plugins.slog.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Slog {

    /**
     * 标签
     * @return
     */
	String tag();
	
	/**
	 * 方法执行前
	 * @return 消息模板
	 */
	String before() default "";
	
	/**
	 * 方法执行后
	 * @return 消息模板
	 */
	String after() default "";
	
	/**
	 * 方法抛出异常时
	 * @return 消息模板
	 */
	String error() default "";
	
	/**
	 * 是否异步执行,默认为true
	 * @return true,如果需要异步执行
	 */
	boolean async() default true;
}
