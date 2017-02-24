package org.nutz.integration.json4excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface J4EFormat {

    /**
     * 转换为大写
     * 
     * @return
     */
    boolean UpperCase() default false;

    /**
     * 转换为小写
     * 
     * @return
     */
    boolean LowerCase() default false;
}
