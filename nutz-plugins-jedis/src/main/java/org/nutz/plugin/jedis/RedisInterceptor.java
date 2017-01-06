package org.nutz.plugin.jedis;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisInterceptor implements MethodInterceptor {

	protected JedisPool jedisPool;
	
	protected static ThreadLocal<Jedis> TL = new ThreadLocal<Jedis>();
	
	public void filter(InterceptorChain chain) throws Throwable {
		if (TL.get() != null) {
			chain.doChain();
			return;
		}
		try (Jedis jedis = jedisPool.getResource()) {
			TL.set(jedis);
			chain.doChain();
		} finally{
			TL.remove();
		}
	}

	public static Jedis jedis() {
		return TL.get();
	}
}
