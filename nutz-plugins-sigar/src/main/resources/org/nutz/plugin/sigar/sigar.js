var ioc = {
		sigarClient : {
			"type" : "org.nutz.plugin.sigar.integration.watchdog.SigarClient",
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