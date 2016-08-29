var ioc = {
		sigarClient : {
			"type" : "org.nutz.sigar.integration.watchdog.SigarClient",
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