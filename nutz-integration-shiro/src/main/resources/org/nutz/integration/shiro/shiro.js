var ioc = {
		shiroEnv : {
			type : "org.nutz.integration.shiro.ioc.NutShiroEnvironmentLoader"
			fields : {
				servletContext : {app:"$servletContext"}
			},
			events : {
				create : "init",
				depose : "depose"
			}
		}
}