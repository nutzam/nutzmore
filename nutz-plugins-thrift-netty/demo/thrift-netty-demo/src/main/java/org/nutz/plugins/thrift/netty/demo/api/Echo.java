package org.nutz.plugins.thrift.netty.demo.api;

import java.io.Closeable;

import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;

/**
 * @author rekoe
 *
 */
@ThriftService
public interface Echo extends Closeable {

	@ThriftMethod
	public String echo(String info);

}
