package org.nutz.shiro.ext.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.shiro.authz.annotation.Logical;
import org.nutz.shiro.InstalledRole;

/**
 * 
 * @author kerbores
 *
 * @email kerbores@gmail.com
 *
 */
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ThunderRequiresRoles {
	Logical logical() default Logical.AND;

	InstalledRole[] value();
}
