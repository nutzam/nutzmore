var ioc = {
		// zbus 内嵌服务器
		mqServer : {
			type : "org.zbus.mq.server.MqServer",
			args : [{refer:"mqServerConfig"}],
			events : {
				create : "start",
				depose : "close"
			}
		},
		mqServerConfig : {
			type:"org.zbus.mq.server.MqServerConfig",
			fields : {
				serverHost : {java:"$conf.get('zbus.serverHost', '0.0.0.0')"},
				serverPort : {java:"$conf.getInt('zbus.serverPort', 15555)"},
				trackServerList : {java:"$conf.get('zbus.trackServerList')"},
				thriftServer : {java:"$conf.get('zbus.thriftServer')"},
				verbose : {java:"$conf.getBoolean('zbus.verbose', false)"},
				selectorCount : {java:"$conf.getInt('zbus.selectorCount', 1)"},
				executorCount : {java:"$conf.getInt('zbus.executorCount', 64)"},
				verbose : {java:"$conf.getBoolean('zbus.verbose', false)"},
				storePath : {java:"$conf.get('zbus.storePath', 'mq')"}
			}
		}
};