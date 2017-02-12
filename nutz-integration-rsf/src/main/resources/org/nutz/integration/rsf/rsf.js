var ioc = {
		rsf : {
			type : "org.nutz.integration.rsf.RsfFactory",
			fields : {
				ioc : {refer:"$ioc"},
				mode : {java:"$conf.get('rsf.mode', 'client')"},
				main : {java:"$conf.get('rsf.main', 'rsf-config.xml')"},
				pkgs : {java:"$conf.get('rsf.packages')"}
			},
			events : {
				create : "init"
			}
		},
		rsfAppContext : {
			type : "net.hasor.core.AppContext",
			factory : "$rsf#getApp",
			events : {
				depose : "shutdown"
			}
		},
		rsfClient : {
			type : "net.hasor.rsf.RsfClient",
			factory : "$rsf#getCleint"
		}
		
};