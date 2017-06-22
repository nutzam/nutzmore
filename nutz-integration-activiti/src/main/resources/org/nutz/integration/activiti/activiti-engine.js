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
			type : "org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration"
			factory: "$conf#make",
			args : ["org.activiti.engine.impl.cfg.StandaloneProcessEngineConfiguration", "activiti."],
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