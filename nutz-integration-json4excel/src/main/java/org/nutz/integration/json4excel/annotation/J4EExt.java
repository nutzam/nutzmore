package org.nutz.integration.json4excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 额外的配置
 * 
 * @author pw
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface J4EExt {

    int passRow() default 0;;

    int passColumn() default 0;;
}
