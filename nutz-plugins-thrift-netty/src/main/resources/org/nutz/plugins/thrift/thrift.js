var ioc = {
	"thriftFactory" : {
		"type" : "org.nutz.plugins.thrift.NutThriftNettyFactory",
		"args" : [ {
			"refer" : "$ioc"
		} ]
	},
	"tTransport" : {
		"type" : "org.apache.thrift.transport.TSocket",
		"singleton" : false,
		"args" : [ {
			java : "$conf.get('thrift.host', 'localhost')"
		}, {
			java : "$conf.getInt('thrift.port', 17424)"
		} ]
	},
	"tCompactProtocol" : {
		"type" : "org.apache.thrift.protocol.TCompactProtocol",
		"singleton" : false,
		"args" : [ {
			"refer" : "$tTransport"
		} ]
	},
	"tCompactProtocol" : {
		type : "org.nutz.plugins.thrift.TCompactProtocolInterceptor",
		fields : {
			tTransport : {
				refer : "tTransport"
			},
			protocol : {
				refer : "tCompactProtocol"
			}
		}
	},
	"tBompactProtocol" : {
		"type" : "org.apache.thrift.protocol.TBompactProtocol",
		"singleton" : false,
		"args" : [ {
			"refer" : "$tTransport"
		} ]
	},
	"tBompactProtocol" : {
		type : "org.nutz.plugins.thrift.TBompactProtocolInterceptor",
		fields : {
			tTransport : {
				refer : "tTransport"
			},
			protocol : {
				refer : "tBompactProtocol"
			}
		}
	},
};