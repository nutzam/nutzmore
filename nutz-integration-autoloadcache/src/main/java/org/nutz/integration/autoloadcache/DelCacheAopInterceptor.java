package org.nutz.integration.autoloadcache;

import java.lang.reflect.Method;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;

import com.jarvis.cache.CacheHandler;
import com.jarvis.cache.annotation.CacheDelete;
import com.jarvis.cache.aop.DeleteCacheAopProxyChain;

public class DelCacheAopInterceptor implements MethodInterceptor {

	private CacheHandler cacheHandler;

	private CacheDelete cache;

	private boolean haveCache;

	public DelCacheAopInterceptor(CacheHandler cacheHandler, CacheDelete cache, Method method) {
		this.cacheHandler = cacheHandler;
		this.cache = cache;
		if (method.isAnnotationPresent(CacheDelete.class)) {
			this.haveCache = true;
		}
	}

	public void filter(final InterceptorChain chain) throws Throwable {
		try {
			if (haveCache) {
				chain.doChain();
				cacheHandler.deleteCache(new DeleteCacheAopProxyChain() {

					@Override
					public Class<?> getTargetClass() {
						return chain.getCallingMethod().getDeclaringClass();
					}

					@Override
					public Method getMethod() {
						return chain.getCallingMethod();
					}

					@Override
					public Object[] getArgs() {
						return chain.getArgs();
					}
				}, cache, chain.getReturn());
			} else {
				chain.doChain();
			}
		} catch (Throwable e) {
			throw e;
		}
	}
}
