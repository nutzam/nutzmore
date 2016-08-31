package org.nutz.plugins.nop.core.serialize;


/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file SerizlizeObject.java
 *
 * @description 可序列化对象
 *
 * @time 2016年8月31日 下午5:07:15
 *
 */
public abstract class SerizlizeObject<T extends SerizlizeObject> {

	public abstract String serialize();

	public abstract T serialize(String data);
}
