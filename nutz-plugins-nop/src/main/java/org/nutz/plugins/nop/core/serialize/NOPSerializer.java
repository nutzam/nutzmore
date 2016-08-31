package org.nutz.plugins.nop.core.serialize;

import org.nutz.lang.Mirror;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-plugins-nop
 *
 * @file UploadFileSerializer.java
 *
 * @description 文件序列化
 *
 * @time 2016年8月31日 下午5:05:25
 *
 */
public class NOPSerializer<T extends SerizlizeObject> implements Serializer<T> {

	Log log = Logs.getLog(NOPSerializer.class);

	private Mirror<T> mirror;
	{
		try {
			Class<T> entryClass = Mirror.getTypeParam(getClass(), 0);
			mirror = Mirror.me(entryClass);
			if (log.isDebugEnabled())
				log.debugf("Get TypeParams for self : %s", entryClass.getName());
		} catch (Throwable e) {
			if (log.isWarnEnabled())
				log.warn("!!!Fail to get TypeParams for self!", e);
		}
	}

	public Class<T> getEntityClass() {
		return mirror.getType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.nutz.plugins.nop.core.serialize.Serializer#getObjClazz()
	 */
	@Override
	public Class<T> getObjClazz() {
		return mirror.getType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.nutz.plugins.nop.core.serialize.Serializer#serialize(java.lang.Object
	 * )
	 */
	@Override
	public String serialize(SerizlizeObject obj) {
		return obj.serialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.nutz.plugins.nop.core.serialize.Serializer#serialize(java.lang.String
	 * )
	 */
	@Override
	public T serialize(String data) {
		return (T) mirror.born().serialize(data);
	}

}
