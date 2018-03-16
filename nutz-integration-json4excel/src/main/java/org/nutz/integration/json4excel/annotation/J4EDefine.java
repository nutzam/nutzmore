package org.nutz.integration.json4excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.integration.json4excel.J4EColumnType;

/**
 * 定义字段类型，方便处理时进行判断
 * 
 * @author pw
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface J4EDefine {

    J4EColumnType type() default J4EColumnType.STRING;

    /**
     * 数字类型才需要设置该参数
     */
    int precision() default 0;

    /**
     * 手动设置index的位置
     */
    int columnIndex() default -1;

    /**
     * 图片导入时才需要设置
     */
    int imgWidth() default 100;

    int imgHeight() default 100;

}
