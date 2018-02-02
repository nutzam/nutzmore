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

    int precision() default 0;

}
