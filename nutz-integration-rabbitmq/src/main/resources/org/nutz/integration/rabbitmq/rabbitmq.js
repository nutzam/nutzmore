var ioc = {
	rabbitmq_cf : {
		type : "com.rabbitmq.client.ConnectionFactory",
		factory : "$conf#make",
		args : ["com.rabbitmq.client.ConnectionFactory", "rabbitmq."]
	},
	rabbitmq : {
		type : "org.nutz.integration.rabbitmq.aop.RabbitmqMethodInterceptor",
		fields : {
			factory : {refer:"rabbitmq_cf"}
		}
	}
};