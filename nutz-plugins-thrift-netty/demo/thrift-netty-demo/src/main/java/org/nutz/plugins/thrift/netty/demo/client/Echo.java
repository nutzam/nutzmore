package org.nutz.plugins.thrift.netty.demo.client;

import java.io.Closeable;

import com.facebook.swift.codec.ThriftField;
import com.facebook.swift.codec.ThriftField.Requiredness;
import com.facebook.swift.service.ThriftMethod;
import com.facebook.swift.service.ThriftService;
import com.google.common.util.concurrent.ListenableFuture;

@ThriftService("Echo")
public interface Echo extends Closeable {
	@ThriftService("Echo")
	public interface Async extends Closeable {
		void close();

		@ThriftMethod(value = "echo")
		ListenableFuture<String> echo(
				@ThriftField(value = 1, name = "info", requiredness = Requiredness.NONE) final String info);
	}

	void close();

	@ThriftMethod(value = "echo")
	String echo(@ThriftField(value = 1, name = "info", requiredness = Requiredness.NONE) final String info)
			throws org.apache.thrift.TException;
}