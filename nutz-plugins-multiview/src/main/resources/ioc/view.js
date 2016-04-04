var ioc = {
	jsp : {
		type : "org.nutz.plugins.view.JspView",
		fields : {
			prefix : "/WEB-INF/templates/jsp",
			suffix : ".jsp"
		}
	},
	btl : {
		type : "org.nutz.plugins.view.BeetlView",
		fields : {
			contentType:"text/html; charset=UTF-8",
			configPath:"WEB-INF/classes",
			prefix : "/templates/btl",
			suffix : ".html"
		}
	},
	jetx : {
		type : "org.nutz.plugins.view.JetTemplateView",
		fields : {
			prefix : "/WEB-INF/templates/jetx",
			suffix : ".html"
		}
	},
	ftl : {
		type : "org.nutz.plugins.view.FreemarkerView",
		fields : {
			prefix : "/WEB-INF/templates/ftl",
			suffix : ".html"
		}
	},
	multiViewResover : {
		type : "org.nutz.plugins.view.MultiViewResover",
		fields : {
			resolvers : {
				"jsp" : {
					refer : "jsp"
				},
				"btl" : {
					refer : "btl"
				},
				"jetx" : {
					refer : "jetx"
				},
				"ftl" : {
					refer : "ftl"
				}
			}
		}
	}
};