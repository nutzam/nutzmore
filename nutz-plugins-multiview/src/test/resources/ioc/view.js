var ioc = {
	conf : {//默认约定的视图配置文件conf
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : [ "custom/" ]
		}
	},
	jsp : {
		type : "org.nutz.plugins.view.JspView",
		args : [ null ],
		fields : {
			prefix : "/WEB-INF/templates/jsp",
			suffix : ".jsp",
		}
	},
	beetl : {
		type : "org.nutz.plugins.view.BeetlView",
		args : [ null ],
		fields : {
			prefix : "/templates/beetl",
			suffix : ".html",
			configPath : "WEB-INF/classes"
		}
	},
	freemarker : {
		type : "org.nutz.plugins.view.FreemarkerView",
		args : [ null ],
		fields : {
			prefix : "/WEB-INF/templates/freemarker",
			suffix : ".html"
		}
	},
	jetTemplate : {
		type : "org.nutz.plugins.view.JetTemplateView",
		args : [ null ],
		fields : {
			prefix : "/WEB-INF/templates/jetTemplate",
			suffix : ".html"
		}
	},
	multiViewResover : {
		type : "org.nutz.plugins.view.MultiViewResover",
		fields : {
			defaultView : "btl",// 默认视图 这里填前缀标识
			config : {// 设置视图的配置文件
				type : "org.nutz.ioc.impl.PropertiesProxy",
				fields : {
					paths : [ "custom/" ]
				}
			}
			resolvers : {
				"jsp" : {// 视图前缀标识
					refer : "jsp"
				},
				"btl" : {// 视图前缀标识
					refer : "beetl"
				},
				"ftl" : {// 视图前缀标识
					refer : "freemarker"
				},
				"jetx" : {// 视图前缀标识
					refer : "jetTemplate"
				}
			}
		}
	}
};