package org.nutz.integration.jedisque;

import com.github.xetorthio.jedisque.Jedisque;
import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.lang.Streams;


public class DisqueInterceptor implements MethodInterceptor {

	protected JedisqueAgent jedisqueAgent;
	
	protected static ThreadLocal<Jedisque> TL = new ThreadLocal<Jedisque>();
	
	public void filter(InterceptorChain chain) throws Throwable {
		if (TL.get() != null) {
			chain.doChain();
			return;
		}
		Jedisque jedisque = null;
		try {
			jedisque = jedisqueAgent.jedisque();
			TL.set(jedisque);
			chain.doChain();
		} finally{
            Streams.safeClose(jedisque);
			TL.remove();
		}
	}
	

	public static Jedisque jedisque() {
		return TL.get();
	}
	
	public void setJedisqueAgent(JedisqueAgent jedisqueAgent) {
        this.jedisqueAgent = jedisqueAgent;
    }
}
