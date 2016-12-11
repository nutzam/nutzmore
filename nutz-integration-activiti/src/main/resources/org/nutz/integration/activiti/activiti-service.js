var ioc = {
		runtimeService : {
			factory : "$processEngine#getRuntimeService"
		},
		repositoryService : {
			factory : "$processEngine#getRepositoryService"
		},
		taskService : {
			factory : "$processEngine#getTaskService"
		},
		managementService : {
			factory : "$processEngine#getManagementService"
		},
		identityService : {
			factory : "$processEngine#getIdentityService"
		},
		historyService : {
			factory : "$processEngine#getHistoryService"
		},
		formService : {
			factory : "$processEngine#getFormService"
		}
};