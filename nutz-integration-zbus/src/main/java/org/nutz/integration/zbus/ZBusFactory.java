package org.nutz.integration.zbus;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.nutz.integration.zbus.mq.ZBusConsumer;
import org.nutz.integration.zbus.mq.ZBusProducer;
import org.nutz.ioc.Ioc;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Streams;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import io.zbus.mq.Broker;
import io.zbus.mq.Consumer;
import io.zbus.mq.ConsumerConfig;
import io.zbus.mq.Message;
import io.zbus.mq.MessageHandler;
import io.zbus.mq.MqClient;
import io.zbus.mq.ProducerConfig;

@IocBean(name="zbus", create="init", depose="close")
public class ZBusFactory implements Closeable {

	private static final Log log = Logs.get();

	protected Set<Consumer> consumers = new HashSet<Consumer>();
	protected Map<String, ZBusProducer> producers = new ConcurrentHashMap<String, ZBusProducer>();
	protected Object lock = new Object();
	protected Broker broker;
	@Inject("refer:$ioc")
	protected Ioc ioc;
	@Inject
	protected PropertiesProxy conf;

	public ZBusProducer getProducer(String mq) {
		ZBusProducer producer = producers.get(mq);
		if (producer == null) {
			synchronized (lock) {
				producer = producers.get(mq);
				if (producer == null) {
					ProducerConfig config = new ProducerConfig(broker);
					producer = new ZBusProducer(config, mq);
					producers.put(mq, producer);
				}
			}
		}
		return producer;
	}
	
	public void init() {
		log.debug("zbus ...");
	}

	public void close() throws IOException {
	}

	public void addConsumer(Class<?> klass) {
		ZBusConsumer z = klass.getAnnotation(ZBusConsumer.class);
		if (z != null && z.enable()) {
			ConsumerConfig mqConfig = fromAnnotation(broker, z);
			proxy(mqConfig, (MessageHandler) ioc.get(klass));
		}
		for (final Method method : klass.getMethods()) {
			z = method.getAnnotation(ZBusConsumer.class);
			if (z != null && z.enable()) {
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

	protected static ConsumerConfig fromAnnotation(Broker broker, ZBusConsumer z) {
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
			Streams.safeClose(c);
			throw new RuntimeException("create Consumer fail obj=" + handler.getClass().getName(), e);
		}
		consumers.add(c);
	}

	public void publish(String topic, Object message) {
		ZBusProducer p = getProducer(topic);
		p.async(message);
	}
}
