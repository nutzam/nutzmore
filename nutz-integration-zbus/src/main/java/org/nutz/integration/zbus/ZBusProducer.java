package org.nutz.integration.zbus;

import java.io.IOException;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.zbus.broker.Broker;
import org.zbus.mq.MqConfig;
import org.zbus.mq.Producer;
import org.zbus.mq.Protocol.MqMode;
import org.zbus.net.Sync.ResultCallback;
import org.zbus.net.http.Message;

public class ZBusProducer extends Producer {

	public ZBusProducer(Broker broker, String mq, MqMode... mode) {
		super(broker, mq, mode);
	}

	public ZBusProducer(MqConfig config) {
		super(config);
	}

	public void async(Object obj) {
		this.async(obj, null);
	}
	
	public void async(Object obj, ResultCallback<Message> callback) {
		Message msg = asMessage(obj);
		if (msg == null)
			return;
		try {
			this.sendAsync(msg, callback);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void sync(Object obj) {
		this.sync(obj, 10000);
	}
	
	public void sync(Object obj, int timeout) {
		Message msg = asMessage(obj);
		if (msg == null)
			return;
		try {
			this.sendSync(msg, timeout);
		} catch (Exception e2) {
            throw Lang.wrapThrow(e2);
        }
	}
	
	public Message asMessage(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof Message) {
			return (Message)obj;
		} else {
			if (obj instanceof String || obj instanceof StringBuilder || obj instanceof StringBuffer)
				return new Message(String.valueOf(obj));
			if (obj instanceof byte[])
				return new Message((byte[])obj);
			return new Message(Json.toJson(obj));
		}
	}
}
