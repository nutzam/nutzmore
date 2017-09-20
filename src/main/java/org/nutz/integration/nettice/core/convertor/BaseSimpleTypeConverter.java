package org.nutz.integration.nettice.core.convertor;

import org.nutz.log.Log;
import org.nutz.log.Logs;

public abstract class BaseSimpleTypeConverter implements ITypeConverter {

	protected final Log logger = Logs.get();

	/**
	 * 实现了ITypeConverter中的相同方法
	 */
	public Object convertValue(Object value, Class<?> toType) {
		return doConvertValue(value, toType);
	}

	protected abstract Object doConvertValue(Object value, Class<?> toType);

}
