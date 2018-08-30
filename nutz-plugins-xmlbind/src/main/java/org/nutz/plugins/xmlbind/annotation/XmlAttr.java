package org.nutz.plugins.xmlbind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注一个XML属性
 * @author wendal(wendal1985@gmail.com)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface XmlAttr {

    /**
     * XML的属性名,默认取java属性名
     */
    String value() default "";
    
    /**
     *  是否忽略本属性
     */
    boolean ignore() default false;
    
    /**
     * 生成XML时遇到null就忽略
     * @return
     */
    boolean ignoreNull() default true;
    
    boolean ignoreZero() default false;
    
    boolean ignoreBlank() default true;
    
    boolean nullAsBlank() default true;
}
