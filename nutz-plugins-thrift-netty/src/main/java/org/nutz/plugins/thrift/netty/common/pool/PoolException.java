package org.nutz.plugins.thrift.netty.common.pool;

/**
 * @author rekoe
 *
 */
public class PoolException extends RuntimeException {
	
	private static final long serialVersionUID = -4140894549712972832L;

	public PoolException(String message) {
		super(message);
	}

	public PoolException(Throwable e) {
		super(e);
	}

	public PoolException(String message, Throwable cause) {
		super(message, cause);
	}
}