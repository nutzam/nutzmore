package org.nutz.integration.shiro.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.shiro.authz.annotation.Logical;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface NutzRequiresPermissions {

	String[] value();

	Logical logical() default Logical.AND;

	/** 是否加入到数据库 */
	boolean enable() default false;

	/** 一级分类中的二级分类 */
	String name();

	/** 一级分类 */
	String tag();

}