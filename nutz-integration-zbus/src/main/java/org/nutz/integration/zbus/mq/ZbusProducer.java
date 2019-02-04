package org.nutz.integration.zbus.mq;

import java.io.IOException;

import org.nutz.json.Json;
import org.nutz.json.JsonFormat;

import io.zbus.mq.Message;
import io.zbus.mq.Producer;
import io.zbus.mq.ProducerConfig;
import io.zbus.transport.ResultCallback;

public class ZbusProducer extends Producer {
	
	protected String topic;

	public ZbusProducer(ProducerConfig producerConfig, String topic) {
		super(producerConfig);
		this.topic = topic;
	}

	public void async(Object obj) {
		this.async(obj, null);
	}
	
	public void async(Object obj, ResultCallback<Message> callback) {
		Message msg = asMessage(obj);
		if (msg == null)
			return;
		if (msg.getTopic() == null)
		    msg.setTopic(topic);
		try {
			this.publishAsync(msg, callback);
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
        if (msg.getTopic() == null)
            msg.setTopic(topic);
		try {
			this.publish(msg, timeout);
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Message asMessage(Object obj) {
		if (obj == null)
			return null;
		if (obj instanceof Message) {
			return (Message)obj;
		} else {
			Message message = new Message();
			if (obj instanceof String || obj instanceof StringBuilder || obj instanceof StringBuffer) {
				message.setBody(obj.toString());
			}
			else if (obj instanceof byte[]) {
				message.setBody((byte[])obj);
			}
			else {
				message.setBody(Json.toJson(obj, JsonFormat.full()));
			}
			message.setTopic(topic);
			return message;
		}
	}
}
