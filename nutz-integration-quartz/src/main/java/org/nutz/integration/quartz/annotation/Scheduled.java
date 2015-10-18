package org.nutz.integration.quartz.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scheduled {

    /**
     * Cron 表达式,优先级最高
     */
    String cron() default "";
    
    // long fixedDelay() default -1;
    
    //String fixedDelayString() default "";
    
    /**
     * 固定频率,单位是秒
     * @return
     */
    int fixedRate() default -1;
    
    //String fixedRateString() default "";
    
    /**
     * 初始延时,单位是秒
     */
    long initialDelay() default -1;
    
    //String initialDelayString() default "";
    
    //String zone() default "";
    
    /**
     * 总运行次数,如果小于1,代表永久运行
     */
    int count() default -1;
}
