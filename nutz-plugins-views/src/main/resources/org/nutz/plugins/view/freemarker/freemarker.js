var ioc = {
	currentTime : {
		type : "org.nutz.plugins.view.freemarker.directive.CurrentTimeDirective"
	},
	currentTimeDirective : {
		type : "org.nutz.plugins.view.freemarker.FreemarkerDirective",
		args : [ "currentTime", {
			refer : "currentTime"
		} ]
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
		}, "WEB-INF", ".ftl", {
			refer : "freemarkerDirective"
		} ]
	},
	freemarkerDirective : {
		type : "org.nutz.plugins.view.freemarker.FreemarkerDirectiveFactory",
		factory : "$freemarkerDirectiveFactory#create",
		args : [ {
			refer : "currentTimeDirective"
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