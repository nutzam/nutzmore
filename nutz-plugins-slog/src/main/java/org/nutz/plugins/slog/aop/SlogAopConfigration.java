package org.nutz.plugins.slog.aop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.nutz.aop.MethodInterceptor;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.aop.SimpleAopMaker;
import org.nutz.plugins.slog.annotation.Slog;

public class SlogAopConfigration extends SimpleAopMaker<Slog> {
	
	public List<? extends MethodInterceptor> makeIt(Slog slog, Method method, Ioc ioc) {
		return Arrays.asList(new SlogAopInterceptor(ioc, slog, method));
	}

	public String[] getName() {
		return new String[0];
	}
	
	public boolean has(String name) {
		return false;
	}
}
