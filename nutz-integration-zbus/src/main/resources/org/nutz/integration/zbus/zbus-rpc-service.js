var ioc = {
		// 服务端配置------------------------
		rpcProcessor:{
			type: "org.zbus.rpc.RpcProcessor"
		},
		serviceConfig : {
			type : "org.zbus.rpc.mq.ServiceConfig",
			args : [{refer:"broker"}],
			fields:{
				mq : {java:"$conf.get('zbus.mq.name', 'nutzbook')"},
				consumerCount : {java:"$conf.getInt('zbus.rpc.service.consumerCount', 2)"},
				messageProcessor : {refer:"rpcProcessor"}
			}
		},
		rpcService : {
			type : "org.zbus.rpc.mq.Service",
			args : [{refer:"serviceConfig"}],
			events : {
				create : "start",
				depose : "close"
			}
		}
		// end 服务器端配置----------------------
};