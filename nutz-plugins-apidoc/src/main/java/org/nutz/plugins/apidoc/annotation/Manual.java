package org.nutz.plugins.apidoc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Manual {

	/**
	 * 给个名字行不行!!
	 * 
	 * @return 文档名称
	 */
	String name();

	/**
	 * 长文本描述信息
	 * 
	 * @return 描述一下这个文档基础的使用方式
	 */
	String description() default "";

	/**
	 * 作者信息
	 */
	String author() default "";

	/**
	 * 
	 * @return 联系邮箱
	 */
	String email() default "";
	/**
	 * 
	 * @return 作者主页
	 */
	String homePage() default "";
	
	String copyRight() default "© 2017 Powered By Nutz.cn.";

}
