var ioc = {
	jsp : {
		type : "org.nutz.plugins.view.JspView",
		fields : {
			name : "JSP",
			prefix : "/WEB-INF/templates/jsp",
			suffix : ".jsp"
		}
	},
	btl : {
		type : "org.nutz.plugins.view.BeetlView",
		fields : {
			name : "Beetl",
			prefix : "/templates/btl",
			suffix : ".html"
		}
	},
	jetx : {
		type : "org.nutz.plugins.view.JetTemplateView",
		fields : {
			name : "JetTemplate",
			prefix : "/WEB-INF/templates/jetx",
			suffix : ".html"
		}
	},
	ftl : {
		type : "org.nutz.plugins.view.FreemarkerView",
		fields : {
			name : "Freemarker",
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