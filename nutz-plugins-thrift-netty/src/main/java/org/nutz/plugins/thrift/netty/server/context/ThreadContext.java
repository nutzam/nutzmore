package org.nutz.plugins.thrift.netty.server.context;

import java.util.HashMap;
import java.util.Map;

import org.nutz.plugins.thrift.netty.server.transport.TNettyTransportContext;

import com.google.common.collect.Maps;

/**
 * @author rekoe
 *
 */
public final class ThreadContext {

	public static final String TRANSPORT_CONTEXT_KEY = "TRANSPORT_CONTEXT_KEY";

	private static final ThreadLocal<Map<Object, Object>> resources = new InheritableThreadLocalMap<Map<Object, Object>>();

	private ThreadContext() {}

	public static void bind(TNettyTransportContext transportContext) {
		if (transportContext != null) {
			put(TRANSPORT_CONTEXT_KEY, transportContext);
		}
	}

	public static TNettyTransportContext unbind() {
		return (TNettyTransportContext) remove(TRANSPORT_CONTEXT_KEY);
	}

	public static TNettyTransportContext getTransportContext() {
		return (TNettyTransportContext) get(TRANSPORT_CONTEXT_KEY);
	}

	public static void put(Object key, Object value) {
		if (key == null) {
			throw new IllegalArgumentException("key cannot be null");
		}

		if (value == null) {
			remove(key);
			return;
		}
		resources.get().put(key, value);
	}

	public static Object get(Object key) {
		Object value = getValue(key);
		return value;
	}

	public static Object remove(Object key) {
		Object value = resources.get().remove(key);
		return value;
	}

	private static Object getValue(Object key) {
		return resources.get().get(key);
	}

	private static final class InheritableThreadLocalMap<T extends Map<Object, Object>>
			extends InheritableThreadLocal<Map<Object, Object>> {
		
		protected Map<Object, Object> initialValue() {
			return Maps.newHashMap();
		}

		@SuppressWarnings({ "unchecked" })
		protected Map<Object, Object> childValue(Map<Object, Object> parentValue) {
			if (parentValue != null) {
				return (Map<Object, Object>) ((HashMap<Object, Object>) parentValue).clone();
			} else {
				return null;
			}
		}
	}
}
