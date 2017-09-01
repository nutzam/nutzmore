var ioc = {
		nutIocElResolver : {
			type : "org.nutz.integration.activiti.NutIocElResolver",
			args : [{refer:"$ioc"}]
		},
		nutzExpressionManager : {
			type : "org.nutz.integration.activiti.NutzExpressionManager",
			fields : {
				nutIocElResolver : {refer:"nutIocElResolver"}
			}
		},
		processEngineSpec : {
			type : "org.nutz.integration.activiti.NutProcessEngineConfiguration"
			factory: "$conf#make",
			args : ["org.nutz.integration.activiti.NutProcessEngineConfiguration", "activiti."],
			fields : {
				dataSource : {refer:"dataSource"},
				expressionManager : {refer:"nutzExpressionManager"}
			}
		},
		processEngine : {
			type : "org.activiti.engine.ProcessEngine",
			factory : "$processEngineSpec#buildProcessEngine"
		}
};