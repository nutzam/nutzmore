package org.nutz.integration.json4excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 时间格式转换
 * 
 * 指定原格式与目标格式，比如yy-MM-dd HH:mm转换为yyyy-MM-dd HH:mm:ss
 * 
 * @author pw
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface J4EDateFormat {

    String from();

    String to();
}
