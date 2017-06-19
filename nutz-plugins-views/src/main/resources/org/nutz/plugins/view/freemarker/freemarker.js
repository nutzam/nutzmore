var ioc = {
	currentTime : {
		type : "org.nutz.plugins.view.freemarker.directive.CurrentTimeDirective"
	},
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
		}, "/WEB-INF", ".ftl", {
			refer : "freemarkerDirectiveFactory"
		} ]
	},
	freemarkerDirectiveFactory : {
		type : "org.nutz.plugins.view.freemarker.FreemarkerDirectiveFactory",
		fields : {
			freemarker : 'org/nutz/plugins/view/freemarker/freemarker.properties'
		}
	}
};
