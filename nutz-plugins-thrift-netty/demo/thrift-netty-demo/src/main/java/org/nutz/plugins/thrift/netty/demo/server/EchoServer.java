package org.nutz.plugins.thrift.netty.demo.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TProtocol;
import org.nutz.ioc.impl.NutIoc;
import org.nutz.ioc.loader.combo.ComboIocLoader;
import org.nutz.lang.Mirror;
import org.nutz.plugins.thrift.NutThriftNettyFactory;
import org.nutz.plugins.thrift.netty.server.ThriftNettyServer;
import org.nutz.plugins.thrift.netty.server.configure.ThriftNettyServerDefBuilder;
import org.nutz.resource.Scans;

import com.facebook.nifty.processor.NiftyProcessor;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftEventHandler;
import com.facebook.swift.service.ThriftService;
import com.facebook.swift.service.ThriftServiceProcessor;
import com.google.common.collect.ImmutableList;

import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;

/**
 * @author rekoe
 *
 */
public class EchoServer {

	/**
	 * @param args
	 * @throws InterruptedException
	 */
	public static void main1(String[] args) throws InterruptedException {
		ResourceLeakDetector.setLevel(Level.ADVANCED); // check memory leak
		List<Class<?>> clazzs = Scans.me().scanPackage("org.nutz.plugins.thrift.netty.demo");
		List<Object> services = new ArrayList<>();
		for (Class<?> clz : clazzs) {
			ThriftService ts = Mirror.getAnnotationDeep(clz, ThriftService.class);
			if (ts != null && !clz.isInterface()) {
				try {
					Object obj = clz.newInstance();
					services.add(obj);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		final NiftyProcessor niftyProcessor = new ThriftServiceProcessor(new ThriftCodecManager(), ImmutableList.<ThriftEventHandler> of(), services);
		TProcessor processor = new TProcessor() {

			@Override
			public boolean process(TProtocol in, TProtocol out) throws TException {

				try {
					return niftyProcessor.process(in, out, null).get();
				} catch (Exception e) {
					throw new TException(e);
				}
			}

		};
		ThriftNettyServer server = new ThriftNettyServer(new ThriftNettyServerDefBuilder().processorFactory(new TProcessorFactory(processor)).build());
		server.start();
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		ResourceLeakDetector.setLevel(Level.ADVANCED);
		NutIoc ioc = new NutIoc(new ComboIocLoader("*anno", "org.nutz.plugins.thrift.netty.demo","*org.nutz.plugins.thrift.ThriftIocLoader"));
		ioc.get(NutThriftNettyFactory.class,"thriftFactory").load("org.nutz.plugins.thrift.netty.demo");
	}
}
