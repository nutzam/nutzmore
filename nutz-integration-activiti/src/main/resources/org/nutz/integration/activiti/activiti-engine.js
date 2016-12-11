var ioc = {
		processEngine : {
			factory: "org.nutz.integration.activiti.ActivitiFactory#build",
			args : [
				{refer:"dataSource"}, {refer:"conf"}
			]
		}
};