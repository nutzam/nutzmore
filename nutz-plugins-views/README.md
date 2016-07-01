# nutzmore Nutz的插件与扩展

### 包含freemarker、velocity、thymeleaf 视图插件

1）freemarker 视图使用方法：

1. 在 MainModule 的 `@IocBy` 中增加 "*org.nutz.plugins.view.freemarker.FreemarkerIocLoader"
1. 在 MainModule 中增加 `@Views(FreemarkerViewMaker.class)`
1. 如需自定义模板相关内容，请复制一份 freemarker.js 到/ioc 目录下，并修改相应内容

```
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
        }, "WEB-INF", ".ftl", {
            refer : "freemarkerDirectiveFactory"
        } ]
    },
    freemarkerDirectiveFactory : {
        type : "org.nutz.plugins.view.freemarker.FreemarkerDirectiveFactory",
        fields : {
            freemarker : 'org/nutz/plugins/view/freemarker/freemarker.properties',
        }
    }
};
```

已自带标签函数`currentTime`，在需要显示的地方自行填加标签 `<@currentTime />`，更多使用方法请参考官方 nutzbook 中的使用配置

如自定义标签加载 请在js中添加

```
mapTags : {
	factory : "$freeMarkerConfigurer#addTags",
	args : [ {
		'abc' : 1,
		'currentTime' : {
			refer : 'currentTime'
		},
		ioc' : {
			refer : '$ioc'
		},
		'conf' : {
			java : '$conf.toMap()'
		},
		'cdnbase' : {
			java : "$conf.get('cdnbase')"
		}
	} ]
}

```

这样可以在模板中直接调用标签
`<@currentTime /> ${rekoe} ${conf['emai.to']} ${abc}`

2）thymeleaf 视图使用方法（现只支持 `2.1.4.RELEASE` 版本）：

1. 在 MainModule 的 `@IocBy` 中增加 "*org.nutz.plugins.view.freemarker.ThymeleafIocLoader"
2. 在 MainModule 中增加 `@Views(ThymeleafViewMaker.class)`
3. 在 `@Ok` 注解中用 `th` 表示使用 thymeleaf 视图渲染，如 `@Ok("th:home/index")`
4. 如需自定义模板相关内容，请创建一份 thymeleaf.js 到/ioc 目录下，并修改 `thymeleafProperties.fields` 中的相应内容

```js
var ioc = {
    thymeleafProperties : {
        type: "org.nutz.plugins.view.thymeleaf.ThymeleafProperties",
        fields: {
            prefix: "/WEB-INF/template/",
            suffix: ".html",
            mode: "HTML5",
            encoding: "UTF-8",
            contentType: "text/html",
            cache: true,
            cacheTTLMs: 3600000
        }
    }
};
```

5. 如需添加其他扩展，只需创建该扩展后注入即可

```js
var ioc = {
    layoutDialect : { type: "nz.net.ultraq.thymeleaf.LayoutDialect" },
    thymeleafProperties : {
        type: "org.nutz.plugins.view.thymeleaf.ThymeleafProperties",
        fields: {
            dialect: [ { refer: "layoutDialect" } ]
        }
    }
};
```
