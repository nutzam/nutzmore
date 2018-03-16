package org.nutz.integration.json4excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.integration.json4excel.J4EEmptyRow;
import org.nutz.integration.json4excel.J4EEmptyRowImpl;

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

    int passContentRow() default 0;;

    int passColumn() default 0;;

    long maxRead() default 0;

    long maxWrite() default 0;

    boolean passHead() default false;

    Class<? extends J4EEmptyRow<?>> passEmptyRow() default J4EEmptyRowImpl.class;
}
