var ioc = {
	// 读取配置文件
	config : {
			type : "org.nutz.ioc.impl.PropertiesProxy",
			fields : { paths : ["SystemGlobals.properties"] } 
	}
};