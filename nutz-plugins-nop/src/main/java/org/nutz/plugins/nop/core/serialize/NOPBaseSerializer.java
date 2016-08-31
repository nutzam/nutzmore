package org.nutz.plugins.nop.core.serialize;

import org.nutz.json.Json;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file UploadFileSerializer.java
 *
 * @description 非特殊对象的序列化处理器
 *
 * @time 2016年8月31日 下午5:05:25
 *
 */
public class NOPBaseSerializer implements Serializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.plugins.nop.core.serialize.Serializer#getObjClazz()
	 */
	@Override
	public Class getObjClazz() {
		return Object.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.nutz.plugins.nop.core.serialize.Serializer#serialize(java.lang.Object
	 * )
	 */
	@Override
	public String serialize(Object t) {
		return Json.toJson(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.nutz.plugins.nop.core.serialize.Serializer#serialize(java.lang.String
	 * )
	 */
	@Override
	public Object serialize(String data) {
		return Json.fromJson(data);
	}

}
