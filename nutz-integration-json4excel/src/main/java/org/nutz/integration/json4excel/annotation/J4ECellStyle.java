package org.nutz.integration.json4excel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nutz.integration.json4excel.J4ECellSetStyle;
import org.nutz.integration.json4excel.J4ECellSetStyleImpl;

/**
 * 
 * @author rekoe
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface J4ECellStyle {

    Class<? extends J4ECellSetStyle> setStyle() default J4ECellSetStyleImpl.class;
}
