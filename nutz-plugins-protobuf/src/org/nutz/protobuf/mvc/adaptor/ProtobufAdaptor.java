package org.nutz.protobuf.mvc.adaptor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.lang.Lang;
import org.nutz.mvc.adaptor.PairAdaptor;
import org.nutz.mvc.adaptor.ParamInjector;
import org.nutz.mvc.adaptor.injector.VoidInjector;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.impl.AdaptorErrorContext;

import com.google.protobuf.ExtensionRegistry;

public class ProtobufAdaptor extends PairAdaptor {

	private ExtensionRegistry registry;

	private final ConcurrentHashMap<Class<?>, Method> methodCache = new ConcurrentHashMap<Class<?>, Method>();

	public ProtobufAdaptor() {
		this.registry = ExtensionRegistry.newInstance();
	}

	public ProtobufAdaptor(ExtensionRegistry registry) {
		this.registry = registry;
	}

	protected ParamInjector evalInjector(Type type, Param param) {
		if (param == null) {
			Class<?> clazz = Lang.getTypeClass(type);
			if (clazz != null && AdaptorErrorContext.class.isAssignableFrom(clazz))
				return new VoidInjector();
			return new ProtobufPairInjector(type, registry, methodCache);
		}
		return super.evalInjector(type, param);
	}
}
