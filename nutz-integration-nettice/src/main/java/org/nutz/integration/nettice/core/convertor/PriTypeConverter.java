package org.nutz.integration.nettice.core.convertor;

import org.nutz.castor.Castors;

public final class PriTypeConverter extends BaseSimpleTypeConverter {

	private static final PriTypeConverter convertor;

	static {
		convertor = new PriTypeConverter();
	}

	/**
	 * 对基本类型进行类型转换
	 */
	protected Object doConvertValue(Object value, Class<?> toType) {
		return Castors.me().castTo(value, toType);
	}

	public static PriTypeConverter getInstance() {
		return convertor;
	}

}
