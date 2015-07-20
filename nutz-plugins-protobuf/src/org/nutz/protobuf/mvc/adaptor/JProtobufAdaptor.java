package org.nutz.protobuf.mvc.adaptor;

import java.lang.reflect.Type;

import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.adaptor.injector.VoidInjector;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.impl.AdaptorErrorContext;

public class JProtobufAdaptor extends PairAdaptor {

	protected ParamInjector evalInjector(Type type, Param param) {
		if (param == null) {
			Class<?> clazz = Lang.getTypeClass(type);
			if (clazz != null && AdaptorErrorContext.class.isAssignableFrom(clazz))
				return new VoidInjector();
			return new JProtobufPairInjector(type);
		}
		return super.evalInjector(type, param);
	}
}
