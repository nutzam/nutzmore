package org.nutz.integration.zbus;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;

import org.nutz.integration.zbus.annotation.ZBusConsumer;
import org.nutz.integration.zbus.annotation.ZBusInvoker;
import org.nutz.integration.zbus.annotation.ZBusService;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;
import org.zbus.broker.Broker;
import org.zbus.mq.Consumer;
import org.zbus.mq.MqConfig;
import org.zbus.mq.Protocol.MqMode;
import org.zbus.net.Client;
import org.zbus.net.core.Session;
import org.zbus.net.http.Message;
import org.zbus.net.http.Message.MessageHandler;
import org.zbus.rpc.RpcProcessor;

public class ZBusFactory {

	private static final Log log = Logs.get();

	protected Set<Consumer> consumers = new HashSet<Consumer>();
	protected Map<String, ZBusProducer> producers = new ConcurrentHashMap<String, ZBusProducer>();
	protected Object lock = new Object();
	protected Broker broker;
	protected Ioc ioc;
	protected List<String> pkgs;

	public ZBusProducer getProducer(String mq) {
		ZBusProducer producer = producers.get(mq);
		if (producer == null) {
			synchronized (lock) {
				producer = producers.get(mq);
				if (producer == null) {
					producer = new ZBusProducer(broker, mq, MqMode.MQ, MqMode.PubSub);
					producers.put(mq, producer);
				}
			}
		}
		return producer;
	}

	public void init() {
		for (String pkg : pkgs) {
			for (Class<?> klass : Scans.me().scanPackage(pkg)) {
				addConsumer(klass);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void close() throws Exception {
		Field clientField = Consumer.class.getDeclaredField("client");
		Field heartbeatorField = Client.class.getDeclaredField("heartbeator");
		clientField.setAccessible(true);
		heartbeatorField.setAccessible(true);
		for (Consumer consumer : consumers) {
			try {
				consumer.close();
			} catch (Exception e) {
				// 下面的代码解决zbus的broker先于consumer关闭,导致Consumer的心跳线程不能关闭的问题
				Client client = (Client) clientField.get(consumer);
				if (client != null) {
					ScheduledExecutorService es = (ScheduledExecutorService) heartbeatorField.get(client);
					if (!es.isShutdown())
						es.shutdown();
				}
			}
		}
	}

	public void addConsumer(Class<?> klass) {
		ZBusConsumer z = klass.getAnnotation(ZBusConsumer.class);
		if (z != null && z.enable()) {
			MqConfig mqConfig = fromAnnotation(broker, z);
			proxy(mqConfig, (MessageHandler) ioc.get(klass));
		}
		for (final Method method : klass.getMethods()) {
			z = method.getAnnotation(ZBusConsumer.class);
			if (z != null && z.enable()) {
				MqConfig mqConfig = fromAnnotation(broker, z);
				final Object obj = ioc.get(klass);
				MessageHandler handler = null;
				switch (method.getParameterTypes().length) {
				case 0:
					handler = new MessageHandler() {
						@Override
						public void handle(Message msg, Session sess) throws IOException {
							try {
								method.invoke(obj);
							} catch (Exception e) {
								throw new RuntimeException(e.getCause());
							}
						}
					};
					break;
				case 1:
					handler = new MessageHandler() {
						@Override
						public void handle(Message msg, Session sess) throws IOException {
							try {
								method.invoke(obj, msg);
							} catch (Exception e) {
								throw new RuntimeException(e.getCause());
							}
						}
					};
					break;
				case 2:
					handler = new MessageHandler() {
						@Override
						public void handle(Message msg, Session sess) throws IOException {
							try {
								method.invoke(obj, msg, sess);
							} catch (Exception e) {
								throw new RuntimeException(e.getCause());
							}
						}
					};
					break;
				default:
					throw new RuntimeException("method[" + method + "] not good");
				}
				proxy(mqConfig, handler);
			}
		}
	}

	protected static MqConfig fromAnnotation(Broker broker, ZBusConsumer z) {
		MqConfig mqConfig = new MqConfig();
		mqConfig.setBroker(broker);
		mqConfig.setMode(z.mode());
		mqConfig.setMq(z.mq());
		mqConfig.setTopic(z.topic());
		mqConfig.setVerbose(z.verbose());
		return mqConfig;
	}

	protected void proxy(MqConfig mqConfig, MessageHandler handler) {
		Consumer c = new Consumer(mqConfig);
		try {
			c.onMessage(handler);
			if (mqConfig.getMode() == MqMode.MQ.intValue())
				c.createMQ();
		} catch (Exception e) {
			Streams.safeClose(c);
			throw new RuntimeException("create Consumer fail obj=" + handler.getClass().getName(), e);
		}
		c.start();
		consumers.add(c);
	}

	public static void buildServices(RpcProcessor rpcProcessor, Ioc ioc, String... pkgs) {
		for (String pkg : pkgs) {
			for (Class<?> klass : Scans.me().scanPackage(pkg)) {
				ZBusService zBusService = klass.getAnnotation(ZBusService.class);
				if (zBusService != null) {
					rpcProcessor.addModule(ioc.get(klass));
				}
			}
		}
	}

	public static void addInovker(Class<?> klass, Map<String, Map<String, Object>> map) {
		ZBusInvoker export = klass.getAnnotation(ZBusInvoker.class);
		if (export != null) {
			String name = export.value();
			if (Strings.isBlank(name)) {
				name = Strings.lowerFirst(klass.getSimpleName());
			}
			log.debugf("define zbus Invoker bean name=%s type=%s", name, klass.getName());
			NutMap _map = new NutMap().setv("factory", "$rpc#getService").setv("args", new String[] { klass.getName() });
			map.put(name, _map);
		}
	}

	public void mq(String mq, Object message) {
		ZBusProducer p = getProducer(mq);
		p.async(message);
	}

	public void publish(String topic, Object message) {
		ZBusProducer p = getProducer(topic);
		p.async(message);
	}
}
