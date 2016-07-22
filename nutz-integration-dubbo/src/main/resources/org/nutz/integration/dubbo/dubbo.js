var ioc = {
   "dubbo_application" : {
	   type : "com.alibaba.dubbo.rpc.config.ApplicationConfig",
	   factory : "$confName#make",
	   args : ["com.alibaba.dubbo.rpc.config.ApplicationConfig", "dubbo.application."]
   },
   "dubbo_registry" : {
	   type : "com.alibaba.dubbo.rpc.config.RegistryConfig",
	   factory : "$confName#make",
	   args : ["com.alibaba.dubbo.rpc.config.RegistryConfig", "dubbo.registry."]
   },
   "dubbo_protocol" : {
	   type : "com.alibaba.dubbo.rpc.config.ProtocolConfig",
	   factory : "$confName#make",
	   args : ["com.alibaba.dubbo.rpc.config.ProtocolConfig", "dubbo.protocol."]
   },
   "dubbo_anno" : {
	   type : "org.nutz.integration.dubbo.DubboAnnotationLoader",
	   factory : "$confName#make",
	   args : ["org.nutz.integration.dubbo.DubboAnnotationLoader", "dubbo.anno."],
	   fields : {
		   ioc : {refer:"$ioc"}
	   }
   },
};