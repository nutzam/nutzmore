package org.nutz.integration.zbus;

import org.nutz.ioc.loader.annotation.AnnotationIocLoader;

/**
 * 集成zbus
 * 
 * @author wendal
 *
 */
public class ZbusIocLoader extends AnnotationIocLoader {
	
	public ZbusIocLoader(){
		super(ZbusIocLoader.class.getPackage().getName());
	}
}
