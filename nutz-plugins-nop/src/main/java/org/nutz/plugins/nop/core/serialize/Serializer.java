package org.nutz.plugins.nop.core.serialize;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file Serializer.java
 *
 * @description 序列化器
 *
 * @time 2016年8月31日 下午4:50:48
 *
 */
public interface Serializer<T> {

	public Class<T> getObjClazz();

	public String serialize(T t);

	public T serialize(String data);

}
