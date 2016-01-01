var ioc = {
	configuration : {
		type : "freemarker.template.Configuration"
	},
	freeMarkerConfigurer : {
		type : "org.nutz.plugins.view.freemarker.FreeMarkerConfigurer",
		events : {
			create : 'init'
		},
		args : [ {
			refer : "configuration"
		}, {
			app : '$servlet'
		}, "WEB-INF", ".ftl", {
			refer : "freemarkerDirectiveFactory"
		} ]
	},
	freemarkerDirectiveFactory : {
		type : "org.nutz.plugins.view.freemarker.FreemarkerDirectiveFactory",
		events : {
			create : 'init'
		},
		fields : {
			freemarker : 'org/nutz/plugins/view/freemarker/freemarker.properties',
		}
	}
};