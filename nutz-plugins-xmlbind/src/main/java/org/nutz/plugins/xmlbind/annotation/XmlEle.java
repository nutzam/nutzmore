package org.nutz.plugins.xmlbind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * XML节点
 * @author wendal
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
@Documented
public @interface XmlEle {

    String value() default "";

    boolean attrsWithAnnotationOnly() default false;
    
    boolean nodesWithAnnotationOnly() default false;

    String extAttrs() default "extAttrs";
    
    String extNodes() default "extNodes";
    
    boolean simpleNode() default false;
    
    boolean ignoreNull() default true;
}
