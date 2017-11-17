package org.nutz.integration.zbus.mq;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将一个类/方法声明为zbus的消费者</p>
 * 如果声明在类上,那必须实现org.zbus.net.http.Message.MessageHandler</p>
 * 如果声明在方法上, 可以是0-2个参数(Message, Session)
 * @author wendal
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZbusConsumer {
	
	boolean enable() default true;

	String mq();
	
	String topic() default "";
	
	boolean verbose() default false;
}
