var ioc = {
	"thriftFactory" : {
		type : "org.nutz.plugins.thrift.NutThriftNettyFactory",
		args : [ {
			"refer" : "$ioc"
		} ]
	},
	"tTransport" : {
		type : "org.apache.thrift.transport.TSocket",
		singleton : false,
		args : [ {
			java : "$conf.get('thrift.host', 'localhost')"
		}, {
			java : "$conf.getInt('thrift.port', 17424)"
		} ]
	},
	"tCompactProtocol" : {
		type : "org.apache.thrift.protocol.TCompactProtocol",
		singleton : false,
		args : [ {
			refer : "tTransport"
		} ]
	},
	"TCOMPACTPROTOCOL" : {
		type : "org.nutz.plugins.thrift.TCompactProtocolInterceptor",
		args : [ {
			refer : "tCompactProtocol"
		} ]
	},
	"tBinaryProtocol" : {
		type : "org.apache.thrift.protocol.TBinaryProtocol",
		singleton : false,
		args : [ {
			refer : "tTransport"
		} ]
	},
	"TBINARYPROTOCOL" : {
		type : "org.nutz.plugins.thrift.TBinaryProtocolInterceptor",
		args : [ {
			refer : "tBinaryProtocol"
		} ]
	}
};