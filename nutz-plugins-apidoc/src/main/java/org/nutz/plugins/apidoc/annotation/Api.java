package org.nutz.plugins.apidoc.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 供ExpUrlMapping读取
 * @author wendal
 * @see ApidocUrlMapping.wendal.nutzbook.mvc.ExpUrlMapping
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Api {
    /**
     * 给个名字行不行!!
     * @return 模块或入口方法的可读名称
     */
    String name();
    
    /**
     * 长文本描述信息
     * @return 描述一下这个方法大概是干啥的
     */
    String description() default "";
    
    ApiParam[] params() default {};
    
    String author() default "";
}
