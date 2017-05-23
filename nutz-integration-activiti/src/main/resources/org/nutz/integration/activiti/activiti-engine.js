var ioc = {
		activitiBeansResolverFactory : {
			type : "org.nutz.integration.activiti.ActivitiNutIocBeansResolverFactory",
			fields : {
				ioc : {refer:"$ioc"}
			}
		},
		processEngineSpec : {
			factory: "org.nutz.integration.activiti.ActivitiFactory#build",
			args : [
				{refer:"dataSource"}, {refer:"conf"}, {refer:"activitiBeansResolverFactory"}
			]
		},
		processEngine : {
			type : "org.activiti.engine.ProcessEngine",
			factory : "$processEngineSpec#buildProcessEngine"
		}
};