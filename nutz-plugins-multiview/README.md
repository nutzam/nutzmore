# nutz-plugins-multiview 多视图插件

简介(可用性:生产,维护者:[邓华锋](http://dhf.ink))
==================================

集合N种模板引擎,可配置性强

###### 适应nutz 1.r.55以上，以下版本暂时未测试

针对 [nutz 没有可以配置视图前缀功能，即配置模板路径](https://github.com/nutzam/nutz/issues/603#issuecomment-35709620) 此问题，开发了此插件。
<br/>目的是用于开发博客社交类等程序，这些可能会经常要更换模板，路径写死在代码不合适。

开发此插件有啥优点了,所有配置都可以在配置文件中实现，而不用硬编码：
-------------------------
* 配置视图的路径
* 配置视图扩展名
* 配置默认视图
* 配置默认内容类型
* 配置字符编码
* 其他扩展属性配置
* 配置全局的属性文件
* 可单独配置视图的属性文件
* 支持session和application级别切换模板路径、后缀及引擎功能

使用步骤：
-------------------------
* 引用nutz-plugins-multiview.jar插件及相关视图的引用包，在pom.xml里有注释引用。
* 配置MainModule的视图为ResourceBundleViewResolver 
```Java
@Views({ResourceBundleViewResolver.class})
```
* 配置json文件，创建view.js文件，内容如下：
```javascript
 var ioc = {
	conf : {//默认约定的视图配置文件conf
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : [ "custom/" ]
		}
	},
	jsp : {
		type : "org.nutz.plugins.view.JspView",
		args : [ null ],//新功能需要个构造参数,必须项
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
			contentType : "text/html",
			characterEncoding : "UTF-8"
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
			config : {// 定制的可单独设置视图的配置文件,优先于conf约定，既如果设置了这个约定conf将失效
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
```
当然要创建对应配置的目录，上面beetl的configPath是指这个视图的配置文件目录，相对于项目根目录来说的，可配置或不配置，分情况而定。 

* 在module中使用情况如下

默认视图使用示例
-------------------------
```Java
@At("/user")
@IocBean
public class UserModule {
	@At
	@Ok("user.index")
	public void index() {

	}
	
	@At
	@Ok("user.info")
	public void info() {

	}
} 
```
默认视图，将会走默认的视图，此例子中走beetl视图，因为上面view.js里配置了defaultView的值为 btl 即此视图的前缀标识。
默认视图的好处是@Ok里不用再加“视图前缀:”来标识，直接通过配置文件就能改变视图模板引擎。

session和application切换视图示例
-------------------------
```Java
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.plugins.view.MultiView;

@IocBean
public class IndexModule {
	@At("/session/change")
	@Ok("index")
	public void sessioinChange() {
		HttpSession session = Mvcs.getHttpSession();
		session.setAttribute(MultiView.DEFAULT_VIEW, "ftl");
		session.setAttribute(MultiView.VIEW_PREFIX, "/templates/freemarker");
		session.setAttribute(MultiView.DEFAULT_SUFFIX, ".html");
	}

	/**
	 * 
	 */
	@At("/application/change")
	@Ok("index")
	public void applicationChange() {
		ServletContext application = Mvcs.getServletContext();
		application.setAttribute(MultiView.DEFAULT_VIEW, "jsp");
		application.setAttribute(MultiView.VIEW_PREFIX, "/templates/jsp");
		application.setAttribute(MultiView.DEFAULT_SUFFIX, ".jsp");
	}
}
```

其他视图使用
-------------------------

```Java
@At("/beetl")
@IocBean
public class BeetlModule {
	@At
	@Ok("btl:index")
	public void index() {

	}
	
	@At
	@Ok("btl:test.index")
	public void test() {

	}
} 
```

```Java
@At("/freemarker")
@IocBean
public class FreemarkerModule {
	@At
	@Ok("ftl:index")
	public void index() {

	}
} 
```

```Java
@At("/jetTemplate")
@IocBean
public class JetTemplateModule {
	@At
	@Ok("jetx:index")
	public void index() {

	}
} 
```

```Java
@At("/jsp")
@IocBean
public class JspModule {
	@At
	@Ok("jsp:index")
	public void index() {

	}
} 
```

访问相应的链接，就会找到相应的视图，

插件的核心类
-------------------------
* ResourceBundleViewResolver 实现的MultiView接口 MultiView继承ViewMaker2，用于从 IOC 容器配置文件中查找视图。
* AbstractTemplateViewResolver 抽象出通用的视图请求操作，填充全局变量，资源路径计算
* MultiViewResover 主要用于注入多视图，设置默认视图，配置文件
* MultiView  MultiView继承ViewMaker2接口，主要在此接口里定义常量

MultiViewResover灵活性
-------------------------
view.js配置文件中，multiViewResover不再是约定的名称，只要定义 org.nutz.plugins.view.MultiViewResover类型，插件都能加载，可灵活定义多个MultiViewResover。


注意
-------------------------

1.如果beetl.properties里设置了RESOURCE.root=WEB-INF ，则view.js配置的beetl视图的路径则在WEB-INF下面。例如

```Java
 #默认配置
#ENGINE=org.beetl.core.FastRuntimeEngine
DELIMITER_PLACEHOLDER_START=${
DELIMITER_PLACEHOLDER_END=}
DELIMITER_STATEMENT_START=<!--#
DELIMITER_STATEMENT_END=-->
DIRECT_BYTE_OUTPUT = FALSE
HTML_TAG_SUPPORT = true
HTML_TAG_FLAG = #
NATIVE_CALL = TRUE
TEMPLATE_CHARSET = UTF-8
ERROR_HANDLER = org.beetl.core.ConsoleErrorHandler
NATIVE_SECUARTY_MANAGER= org.beetl.core.DefaultNativeSecurityManager
RESOURCE_LOADER=org.beetl.core.resource.ClasspathResourceLoader
MVC_STRICT = FALSE
#资源配置，resource后的属性只限于特定ResourceLoader
#classpath 根路径
RESOURCE.root= WEB-INF
#是否检测文件变化
RESOURCE.autoCheck=true
```

2.jetbrick-template.properties里配置的路径被view.js配置的路径给覆盖。


```Java
 template.loader = $loader

$loader = jetbrick.template.loader.ResourceLoader
$loader.root = /WEB-INF
$loader.reloadable =false
```

既这时候的root路径不起作用。

插件中包含了对beetl、freemarker、JetTemplate和jsp的视图实现，其实把这些代码抽离出来，按需使用可让插件体积更小。



如果想添加别的视图，只需继承于AbstractTemplateViewResolver，实现init和render方法即可。
<br/>例如以下是在此插件下beetl模板视图的实现：

```Java
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.WebAppResourceLoader;
import org.beetl.ext.web.WebRender;
import org.nutz.lang.Strings;

public class BeetlView extends AbstractTemplateViewResolver {

    public BeetlView(String dest) {
        super(dest);
    }

    public GroupTemplate groupTemplate;

    @Override
    protected void init(String appRoot, ServletContext sc) {
        Configuration cfg = null;
        try {
            cfg = Configuration.defaultConfiguration();
            // 针对beetl放在公共的lib目录获取不到beetl.properties的补救方案
            if (!Strings.isBlank(appRoot) && !Strings.isBlank(getConfigPath())) {
                cfg.add(new File(appRoot + "/" + getConfigPath() + "/beetl.properties"));
            }
        } catch (IOException e) {
            throw new RuntimeException("加载GroupTemplate失败", e);
        }
        WebAppResourceLoader resourceLoader = new WebAppResourceLoader();
        if (!Strings.isBlank(appRoot)) {
            resourceLoader.setRoot(appRoot);
        }
        groupTemplate = new GroupTemplate(resourceLoader, cfg);
        // 3.0以上用sc.getClassLoader()
        // 2.5以下用Thread.currentThread().getContextClassLoader()
        groupTemplate.setClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public void render(HttpServletRequest req, HttpServletResponse resp, String evalPath,
            Map<String, Object> sharedVars) throws Throwable {
        groupTemplate.setSharedVars(sharedVars);
        WebRender render = new WebRender(groupTemplate);
        render.render(evalPath, req, resp);
    }
    
}
```
<br/>注意上面的getConfigPath()方法的代码，是为了实现beetl放在公共的lib目录获取不到beetl配置文件的问题而补救的一个解决方案，configPath是父类AbstractTemplateViewResolver 的属性,可在ioc配置文件中配置。 
<br/>init方法只执行一次，一般用于加载视图的配置相关的代码，且某些对象只需实例化一次，后面就不用实例化。
<br/>render方法的sharedVars是全局的变量。

内置变量
-------------------------
* path 项目根路径
* 完整的项目连接路径basePath
* 请求连接的后缀servletExtension
* 模板所在目录tplDir
* 资源根路径resPath
* 模板对应的资源路径tplResPath
* 当前系统环境props
* 国际语言msgs
* 所有配置文件信息cfg
* 还有个viewName变量，用于显示使用的模板视图的名称。

这几个变量对于做博客论坛等经常更换模板的程序很有用。 

ResourceBundleViewResolver源码片段

```Java
String resDir = config.get(RESOURCE_DIR);
if (Strings.isBlank(resDir)) {
   resDir = "";
 }
String path = req.getContextPath();
int serverPort = req.getServerPort();
String basePath = req.getScheme() + "://" + req.getServerName()
     + (serverPort != 80 ? ":" + serverPort : "") + path
    + "/";
sv.put(PATH, path);
sv.put(BASE_PATH, basePath);
sv.put(SERVLET_EXTENSION, config.get(SERVLET_EXTENSION_KEY));
sv.put(TPL_DIR, tplDir);
if (!resDir.startsWith("http")) {// 如果是http开头，说明是CDN静态地址
   resDir = path + "/" + resDir;
}
sv.put(RES_PATH, resDir);// 资源路径
sv.put(TPL_RES_PATH,resDir + tplDir.replace(WEB_INF, "") + "/");// 模板对应的资源路径
vr.render(req, resp, evalPath, sv);

```

如果配置文件中增加如下代码：
旧：
```javascript
 // 读取配置文件
	conf : {
			type : "org.nutz.ioc.impl.PropertiesProxy",
			fields : { paths : ["SystemGlobals.properties"] } 
	}

```
注意：以上方式已经废弃，不起作用，请使用以下新方式

新：
```javascript
multiViewResover : {
		type : "org.nutz.plugins.view.MultiViewResover",
		fields : {
			defaultView : "btl",// 默认视图 这里填前缀标识
			config : {// 定制的可单独设置视图的配置文件,优先于conf约定，既如果设置了这个约定conf将失效
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
```

SystemGlobals.properties属性文件中，可配置如下:

```Java
 # If you change this value, is necessary to edit WEB-INF/web.xml as well
servlet.extension=
resource.dir=resources

```


servlet.extension 是请求连接的后缀 对应页面变量servletExtension

resource.dir  资源根路径 对应页面变量resPath,此路径可以设置为静态资源链接地址，例如：http://static.denghuafeng.com


# 功能改进计划：
###### 1.支持全局变量的添加，例如，把一些字典的类加载到全局里，供页面调用。
###### 2.支持Ajax视图，此需要特殊处理。例如，前段Ajax请求，需要处理获取的数据、本次请求状态、提示信息等。
###### 3.增加对pdf、velocity、thymeleaf、captcha视图支持，增强Freemarker视图可配置性。 

# 更新日志：
# 1.66版本：
###### 1.增加session和application级别切换模板路径、后缀及引擎功能
###### 2.增加MultiViewResover获取方式的灵活性
###### 3.去除约定的获取MultiViewResover和conf的方式

# 1.65版本：
###### 1.增加默认视图设置
###### 2.增加配置视图扩展属性
###### 3.增加全局属性文件的可配置功能

# 1.65之前版本：
###### 1.把所有属性配置文件里的变量及值加载到全局里，例如，在配置文件中配置了CDN地址，想在页面中能调用，默认全局变量是cfg变量，通过cfg调用相应的键值。
###### 2.支持直接调用视图 AbstractTemplateViewResolver atvr=new org.nutz.plugins.view.JspView("abc.bcd");
###### 3.代码重构，去除了AbstractUrlBasedView.java和ViewResolver
