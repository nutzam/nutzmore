package org.nutz.plugins.mongo;

import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mongo.ZMongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

@IocBean
public class MongoBeanMaker {
	
	@Inject
	protected PropertiesProxy conf;
	
	@IocBean(name="mongoClient")
	public MongoClient createMongoClient() {
		MongoClientOptions.Builder builder = MongoClientOptions.builder();
		builder.maxWaitTime(1500);
		builder.connectionsPerHost(1000);
		MongoClientOptions clientOptions = builder.build();
		ServerAddress address = new ServerAddress(conf.get("mongo.host"), conf.getInt("mongo.port"));
		return new MongoClient(address, clientOptions);
	}

	@IocBean(name="zMongo")
	public ZMongo createZMongo(@Inject MongoClient client) {
		return ZMongo.me(client);
	}
}
