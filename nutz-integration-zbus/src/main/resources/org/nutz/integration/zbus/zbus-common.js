var ioc = {
		// zbus 服务信息配置
		brokerConfig : {
			type : "org.zbus.broker.BrokerConfig",
			fields:{
				"brokerAddress" : {java:"$conf.get('zbus.serverAddr', '127.0.0.1:15555')"}
			}
		},
		broker : {
			type : "org.zbus.broker.SingleBroker",
			args : [{refer:"brokerConfig"}],
			events : {
				depose : "close"
			}
		},
		zbus : {
			type: "org.nutz.integration.zbus.ZBusFactory",
			fields : {
				//pkgs : ["net.wendal.nutzbook"],
				ioc : {refer:"$ioc"},
				broker : {refer:"broker"}
			},
			events : {
				create : "init",
				depose : "close"
			}
		}
};