package org.nutz.integration.zbus;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.integration.zbus.mq.ZbusConsumer;
import org.nutz.integration.zbus.mq.ZbusProducer;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.Scans;

import io.zbus.mq.Broker;
import io.zbus.mq.Consumer;
import io.zbus.mq.ConsumerConfig;
import io.zbus.mq.Message;
import io.zbus.mq.MessageHandler;
import io.zbus.mq.MqClient;
import io.zbus.mq.ProducerConfig;

@IocBean(name="zbus", create="init", depose="close")
public class ZbusFactory implements Closeable {

	private static final Log log = Logs.get();

	protected Set<Consumer> consumers = new HashSet<Consumer>();
	protected Map<String, ZbusProducer> producers = new ConcurrentHashMap<String, ZbusProducer>();
	protected Object lock = new Object();
	@Inject(value="refer:zbusBroker", optional=true)
	protected Broker broker;
	@Inject("refer:$ioc")
	protected Ioc ioc;
	@Inject
	protected PropertiesProxy conf;

	public ZbusProducer getProducer(String mq) {
		ZbusProducer producer = producers.get(mq);
		if (producer == null) {
			synchronized (lock) {
				producer = producers.get(mq);
				if (producer == null) {
					ProducerConfig config = new ProducerConfig(broker);
					producer = new ZbusProducer(config, mq);
					producers.put(mq, producer);
				}
			}
		}
		return producer;
	}
	
	public void init() {
	    String pkgs = conf.get("zbus.mq.packageNames");
	    if (Strings.isBlank(pkgs))
	        return;
	    for (String pkg : Strings.splitIgnoreBlank(pkgs)) {
	        for (Class<?> klass : Scans.me().scanPackage(pkg)) {
	            addConsumer(klass);
	        }
        }
		
	}

	public void close() throws IOException {
	}

	public void addConsumer(Class<?> klass) {
		ZbusConsumer z = klass.getAnnotation(ZbusConsumer.class);
		if (z != null && z.enable()) {
		    log.debug("add @ZbusConsumer " + klass.getName());
			ConsumerConfig mqConfig = fromAnnotation(broker, z);
			proxy(mqConfig, (MessageHandler) ioc.get(klass));
			return;
		}
		for (final Method method : klass.getMethods()) {
			z = method.getAnnotation(ZbusConsumer.class);
			if (z != null && z.enable()) {
			    log.debug("add @ZbusConsumer " + method);
				ConsumerConfig mqConfig = fromAnnotation(broker, z);
				final Object obj = ioc.get(klass);
				MessageHandler handler = null;
				switch (method.getParameterTypes().length) {
				case 0:
					handler = new MessageHandler() {
						public void handle(Message msg, MqClient client) throws IOException {
						    try {
                                method.invoke(obj);
                            }
                            catch (Exception e) {
                                throw new IOException(e);
                            }
						}
					};
					break;
				case 1:
					handler = new MessageHandler() {
					    public void handle(Message msg, MqClient client) throws IOException {
                            try {
                                method.invoke(obj, msg);
                            }
                            catch (Exception e) {
                                throw new IOException(e);
                            }
                        }
					};
					break;
				case 2:
				    handler = new MessageHandler() {
                        public void handle(Message msg, MqClient client) throws IOException {
                            try {
                                method.invoke(obj, msg, client);
                            }
                            catch (Exception e) {
                                throw new IOException(e);
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

	protected static ConsumerConfig fromAnnotation(Broker broker, ZbusConsumer z) {
		ConsumerConfig config = new ConsumerConfig(broker);
		config.setTopic(z.topic());
		config.setVerbose(z.verbose());
		return config;
	}

	protected void proxy(ConsumerConfig consumerConfig, MessageHandler handler) {
		Consumer c = new Consumer(consumerConfig);
		try {
	        c.start(handler);
		} catch (Exception e) {
		    e.printStackTrace();
			Streams.safeClose(c);
			throw new RuntimeException("create Consumer fail obj=" + handler.getClass().getName(), e);
		}
		consumers.add(c);
	}

	public void publish(String topic, Object message) {
		ZbusProducer p = getProducer(topic);
		p.async(message);
	}
}
