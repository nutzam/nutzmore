package org.nutz.plugins.validation.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 验证支持注解。
 * 支持 13 种常见的验证方法。允许多种方法混合验证，并可以自定义验证方法
 * 
 * @see AnnotationValidation
 * @see ValidationUtils
 * 
 * @author QinerG(QinerG@gmail.com)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.PARAMETER, ElementType.FIELD })
public @interface Validations {

	/**
	 * 必填字段验证规则
	 */
	public boolean required() default false;

	/**
	 * 手机号验证规则
	 */
	public boolean mobile() default false;

	/**
	 * 帐号验证规则(字母开头，允许字母数字下划线)，常与字串长度验证规则混合使用
	 */
	public boolean account() default false;

	/**
	 * Email 验证规则
	 */
	public boolean email() default false;

	/**
	 * QQ 号验证规则
	 */
	public boolean qq() default false;

	/**
	 * 字串必须为中文验证规则
	 */
	public boolean chinese() default false;

	/**
	 * 邮政编码验证规则
	 */
	public boolean post() default false;

	/**
	 * 正则表达式验证规则
	 */
	public String regex() default "";

	/**
	 * 重复性验证规则。请放置待比较的字段名
	 */
	public String repeat() default "";

	/**
	 * 字符串最大、最小长度验证规则
	 */
	public int[] strLen() default {};

	/**
	 * 数值型数据取值范围区间验证规则，兼容 int、long、float、double
	 */
	public double[] limit() default {};
	
	/**
	 * 通过nutz自带的el表达式进行验证。注意该表达式返回值应该为布尔型<br>
	 * 例如：value*10<100 
	 */
	public String el() default "";

	/**
	 * 自定义效验规则,可以自行指定验证的方法名称 <br/> 该方法必须是public的，且没有参数返回值为boolean型
	 */
	public String custom() default "";

	/**
	 * 错误提示语
	 */
	public String errorMsg();
}
