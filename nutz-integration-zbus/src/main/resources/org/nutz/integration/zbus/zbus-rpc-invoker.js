var ioc = {
		// 客户端配置--------------------------
		mqInvoker :{
			type : "org.zbus.rpc.mq.MqInvoker",
			args : [
				{refer:"broker"},
				{java:"$conf.get('zbus.mq.name', 'nutzbook')"}
			]
		},
		rpc : {
			type : "org.zbus.rpc.RpcFactory",
			args : [{refer:"mqInvoker"}]
		},
		// 定义各种代理接口, 如无特殊需要, 使用zbus-*.json时,仅需要修改下面的代理接口定义
		// 一般情况下可以通过注解@ZBusInvoker,自动注册(通过ZBusIocLoader扫描)
		/*
		sayHelloWorld : {
			//type : "net.wendal.nutzbook.zbus.SayHelloWorld", // 被代理的接口,不需要写
			factory : "$rpc#getService", // $rpc 指向rpc这个bean(即上面声明的RpcFactory的实例), 通过其getService方法获取
			args : ["net.wendal.nutzbook.zbus.SayHelloWorld"] // 这里需要传被代理的接口的名称
		}
		*/
		// end 定义各种代理接口
		// end 客户端配置---------------------
};