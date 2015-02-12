package org.nutz.plugins.aop.interceptor;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.lang.Stopwatch;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class ExecutionTimeInterceptor implements MethodInterceptor {

	private static Log LOG = Logs.getLog(ExecutionTimeInterceptor.class);
	
	public void filter(InterceptorChain chain) throws Throwable {
		if (!LOG.isDebugEnabled()) {
			chain.doChain();
			return;
		}
		Stopwatch stopwatch = Stopwatch.begin();
		try {
		    chain.doChain();
		} finally {
	        stopwatch.stop();
	        LOG.debugf("%s [%dms]", chain.getCallingMethod(), stopwatch.getDuration());
		}
	}

}
