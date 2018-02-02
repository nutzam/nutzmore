package org.nutz.integration.json4excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.integration.json4excel.J4ECellFromExcel;
import org.nutz.integration.json4excel.J4ECellFromExcelImpl;
import org.nutz.integration.json4excel.J4ECellToExcel;
import org.nutz.integration.json4excel.J4ECellToExcelImpl;

/**
 * 字段处理方法，toExcel跟fromExcel全部有自己来实现
 * 
 * @author pw
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface J4ECell {

    Class<? extends J4ECellToExcel> toExcel() default J4ECellToExcelImpl.class;

    Class<? extends J4ECellFromExcel> fromExcel() default J4ECellFromExcelImpl.class;
}
