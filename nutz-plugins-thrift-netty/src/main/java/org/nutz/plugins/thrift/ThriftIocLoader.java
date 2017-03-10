package org.nutz.plugins.thrift;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TProtocol;
import org.nutz.castor.Castors;
import org.nutz.ioc.IocException;
import org.nutz.ioc.IocLoading;
import org.nutz.ioc.ObjectLoadException;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.ioc.loader.json.JsonLoader;
import org.nutz.ioc.meta.IocObject;
import org.nutz.json.Json;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.Mvcs;
import org.nutz.plugins.thrift.netty.server.ThriftNettyServer;
import org.nutz.plugins.thrift.netty.server.configure.ThriftNettyServerDefBuilder;
import org.nutz.resource.Scans;

import com.facebook.nifty.processor.NiftyProcessor;
import com.facebook.swift.codec.ThriftCodecManager;
import com.facebook.swift.service.ThriftEventHandler;
import com.facebook.swift.service.ThriftService;
import com.facebook.swift.service.ThriftServiceProcessor;
import com.google.common.collect.ImmutableList;

/**
 * @author rekoe
 *
 */
public class ThriftIocLoader extends JsonLoader {

	private static final Log log = Logs.get();

	private Map<String, Object> map = new HashMap<String, Object>();

	public ThriftIocLoader(String... packages) {
		for (String packageZ : packages) {
			for (Class<?> classZ : Scans.me().scanPackage(packageZ))
				addClass(classZ);
		}
		if (map.size() > 0) {
			if (log.isInfoEnabled())
				log.infof("Found %s classes in %s base-packages!\nbeans = %s", map.size(), packages.length, Castors.me().castToString(map.keySet()));
			final NiftyProcessor niftyProcessor = new ThriftServiceProcessor(new ThriftCodecManager(), ImmutableList.<ThriftEventHandler> of(), map.values());
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
			try {
				server.start();
			} catch (InterruptedException e) {
				log.error(e);
			}
		} else {
			log.warn("NONE Annotation-Class found!! Check your ioc configure!! packages=" + Arrays.toString(packages));
		}
	}

	protected void addClass(Class<?> classZ) {
		if (classZ.isInterface())
			return;
		int modify = classZ.getModifiers();
		if (Modifier.isAbstract(modify) || (!Modifier.isPublic(modify)))
			return;
		ThriftService ts = Mirror.getAnnotationDeep(classZ, ThriftService.class);
		if (ts == null) {
			return;
		}
		String beanName = Strings.lowerFirst(classZ.getSimpleName());
		IocBean iocBean = classZ.getAnnotation(IocBean.class);
		if (iocBean != null) {
			Object obj = Mvcs.getIoc().get(classZ);
			if (obj != null) {
				map.put(beanName, obj);
			} else {
				throw new IocException(beanName, "Ioc beanName=%s, %s null", beanName, classZ.getName());
			}
		} else {
			try {
				Object obj = classZ.newInstance();
				if (has(beanName))
					throw new IocException(beanName, "Duplicate beanName=%s, by %s !!", beanName, classZ.getName());
				map.put(beanName, obj);
			} catch (InstantiationException e) {
				log.error(e);
			} catch (IllegalAccessException e) {
				log.error(e);
			}
		}
	}

	public String[] getName() {
		return map.keySet().toArray(new String[map.size()]);
	}

	public boolean has(String name) {
		return map.containsKey(name);
	}

	public String toString() {
		return "/*ThriftIocLoader*/\n" + Json.toJson(map);
	}

	@Override
	public IocObject load(IocLoading loading, String name) throws ObjectLoadException {
		if (getMap().containsKey(name))
			return super.load(loading, name);
		throw new ObjectLoadException("Object '" + name + "' without define!");
	}
}
