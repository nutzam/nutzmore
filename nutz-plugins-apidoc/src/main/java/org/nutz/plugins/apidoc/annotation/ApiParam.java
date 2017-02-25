package org.nutz.plugins.apidoc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiParam {

    /**
     * 参数名称
     */
    String name() default "";
    
    /**
     * 描述
     */
    String description() default "";
    
    /**
     * 类型
     */
    String type() default "";
    
    /**
     * 缺省值
     */
    String defaultValue() default "";
    
    /**
     * 日期格式
     */
    String dateFormat() default "";
    
    /**
     * 是否可选
     */
    boolean optional() default false;
    
    /**
     * 参数索引,用于匹配方法参数
     */
    int index() default -1;
    
    /**
     * 是否忽略
     */
    boolean ignore() default false;
    
    /**
     * 
     * @return 请求数据示例
     */
    String requestData() default "";
    
}
