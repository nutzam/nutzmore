var ioc = {
		sigarClient : {
			"type" : "org.nutz.plugins.sigar.integration.watchdog.SigarClient",
		      "events" :{
		         "create" :"init"
		      },
		      "fields" :{
		         "config" :{
		            "refer" :"@confName"
		         }
		      }
		}
}