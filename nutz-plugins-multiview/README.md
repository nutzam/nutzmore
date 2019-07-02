[nutz-plugins-multiview 多视图插件](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview)
====

[快速上手](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#%E5%BF%AB%E9%80%9F%E4%B8%8A%E6%89%8B) | [版本历史](https://github.com/nutzam/nutzmore/blob/master/nutz-plugins-multiview/CHANGELOG.md)

# 简介(可用性:生产,维护者:[邓华锋](http://dhf.ink))

集合N种模板引擎,可配置性强。

针对 [nutz 没有可以配置视图前缀功能，即配置模板路径](https://github.com/nutzam/nutz/issues/603#issuecomment-35709620) 此问题，开发了此插件。

目的是用于网站可通过配置文件就能切换模板引擎及网站主题皮肤等，路径等配置项写死在代码不合适。

优点：所有配置都可以在配置文件中实现，而不用硬编码。

# 功能

## 支持多种配置项

目前支持7种[配置项](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#完整配置示例)：

1. 视图的路径
2. 视图文件扩展名
3. [默认视图](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#%E9%BB%98%E8%AE%A4%E8%A7%86%E5%9B%BE%E4%BD%BF%E7%94%A8%E7%A4%BA%E4%BE%8B)
4. 默认内容类型
5. 字符编码
6. [动态可扩展属性配置](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#thymeleaf)
7. 单个视图的属性文件配置

## 支持在线切换模板引擎

支持两种模式的[在线切换](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#session和application切换视图示例)模板引擎、视图路径、视图文件扩展名：

1. [session](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#session和application切换视图示例)
2. [application](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#session和application切换视图示例)

## 支持N种模板引擎

| 模板引擎                                                     | 描述                                                     | 是否支持 |
| ------------------------------------------------------------ | -------------------------------------------------------- | -------- |
| [Beetl](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#beetl) | 新一代典范，功能强大，性能良好，易学易用。               | Yes      |
| [Thymeleaf](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#thymeleaf) | XML/XHTML/HTML5模板引擎，可用于Web与非Web环境中。        | Yes      |
| [Freemarker](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#freemarker) | 一个基于模板生成文本输出的通用工具。                     | Yes      |
| [JetTemplate](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#JetTemplate) | 新一代，具有高性能和高扩展性。                           | Yes      |
| [Captcha](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#Captcha) | 验证码生成                                               | Yes      |
| [Velocity](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#velocity) | 允许网页设计者引用Java代码中定义的方法。                 | Yes      |
| [Jsp](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#Jsp) | java服务器页面，简化的Servlet设计,一种动态网页技术标准。 | Yes      |

更多模板引擎支持等你来[实现扩展](https://github.com/nutzam/nutzmore/tree/master/nutz-plugins-multiview#实现扩展)。

# 快速上手

1. 应用此插件jar包及要使用相关模板引擎的jar包，下载： [maven](http://repo1.maven.org/maven2/org/nutz/nutz-plugins-multiview/)  [最新JAR包](https://search.maven.org/remote_content?g=org.nutz&a=nutz-plugins-multiview&v=LATEST)
2. 配置json文件，创建view.js文件，配置模板引擎相关配置 

3. 配置MainModule的视图为ResourceBundleViewResolver 
```Java
@Views({ResourceBundleViewResolver.class})
```
4. 在module中使用

### 详细如下 

#### 必须依赖

```xml
<dependency>
	<groupId>javax.servlet</groupId>
	<artifactId>javax.servlet-api</artifactId>
	<version>3.1.0</version>
	<scope>provided</scope>
</dependency>
<dependency>
    <groupId>org.nutz</groupId>
    <artifactId>nutz-plugins-multiview</artifactId>
    <version>1.r.67</version>
</dependency>
```

#### Beetl

```xml
<dependency>
	<groupId>com.ibeetl</groupId>
	<artifactId>beetl</artifactId>
	<version>3.0.7.RELEASE</version>
</dependency>
```
假如模板页面所在路径是WEB-INFO/templates/beetl,有如下页面：

1. index.html
2. test/index.html

如果beetl.properties,配置如下项:

```
#classpath 根路径
RESOURCE.root= WEB-INF
```

则view.js配置的beetl视图的路径则在WEB-INF下面了，json配置：

```javascript
beetl : {
		type : "org.nutz.plugins.view.BeetlView",
		args : [ null ],
		fields : {
			prefix : "/templates/beetl",
			suffix : ".html",
			configPath : "WEB-INF/classes",//是指这个视图的配置文件目录，相对于项目根目录来说的，可配置或不配置，分情况而定。 
			contentType : "text/html",
			characterEncoding : "UTF-8",
            config: {//可指定此视图的配置文件
				type : "org.nutz.ioc.impl.PropertiesProxy",
				fields : {
					paths : [ "beetl.properties" ]
				}
			}
		}
	}
```

配置multiViewResover：

```javascript
multiViewResover : {
		type : "org.nutz.plugins.view.MultiViewResover",
		fields : {
            defaultView : "btl",// 默认视图 这里填前缀标识
			config : {//视图的属性文件配置
				refer : "conf"
			},
			resolvers : {
				"btl" : {// 视图前缀标识
					refer : "beetl"
				}
			}
		}
	}
```



Java

```java
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



#### Thymeleaf

```xml
<dependency>
	<groupId>org.thymeleaf</groupId>
	<artifactId>thymeleaf</artifactId>
	<version>3.0.11.RELEASE</version>
</dependency>
<dependency>
	<groupId>nz.net.ultraq.thymeleaf</groupId>
	<artifactId>thymeleaf-layout-dialect</artifactId>
	<version>2.3.0</version>
</dependency>
<!--如果要使用时间插件，需要配置如下-->
<dependency>
	<groupId>org.thymeleaf.extras</groupId>
	<artifactId>thymeleaf-extras-java8time</artifactId>
	<version>3.0.3.RELEASE</version>
</dependency>
```

假如模板页面所在路径是WEB-INFO/templates/thymeleaf,有如下页面：

1. index.html
2. test/index.html

json配置

```javascript
java8TimeDialect : {
		type : "org.thymeleaf.extras.java8time.dialect.Java8TimeDialect"
	},
thymeleaf : {
		type : "org.nutz.plugins.view.ThymeleafView",
		args : [ null ],
		fields : {
			prefix : "/WEB-INF/templates/thymeleaf",
			suffix : ".html",
			contentType : "text/html",
			encoding : "UTF-8",
			properties : {
				type : "org.nutz.lang.util.NutMap",
				args : [ {//动态可扩展属性配置
					"templateMode" : "HTML5",
					"cacheable" : true,
					"cacheTTLMs" : 3600000,
					"dialects" : [{
						refer : "java8TimeDialect"
					}] //可配置多个插件引用用逗号分隔
				} ]
			}
		}
	}
```

配置multiViewResover：

```javascript
multiViewResover : {
		type : "org.nutz.plugins.view.MultiViewResover",
		fields : {
            defaultView : "th",// 默认视图 这里填前缀标识
			config : {//视图的属性文件配置
				refer : "conf"
			},
			resolvers : {
				"th" : {// 视图前缀标识
					refer : "thymeleaf"
				}
			}
		}
	}
```



java

```java
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.plugins.view.ResourceBundleViewResolver;

@IocBean()
@At("/thymeleaf")
public class ThymeleafModule {
	@At
	@Ok("th:index")
	public void index() {
		Mvcs.getReq().setAttribute("thText", "th:text 设置文本内容 <b>加粗</b>");
	}
    
    @At
	@Ok("th:test.index")
	public void testIndex() {
		Mvcs.getReq().setAttribute("thText", "th:text 设置文本内容 <b>test</b>");
	}
}
```

#### Freemarker

```xml
<dependency>
	<groupId>org.freemarker</groupId>
	<artifactId>freemarker</artifactId>
	<version>2.3.28</version>
	<scope>compile</scope>
</dependency>
```

假如模板页面所在路径是/WEB-INF/templates/freemarker,有如下页面：

1. index.html
2. test/index.html

json配置

```javascript
freemarker : {
		type : "org.nutz.plugins.view.FreemarkerView",
		args : [ null ],
		fields : {
			prefix : "/WEB-INF/templates/freemarker",
			suffix : ".html"
		}
	}
```

配置multiViewResover：

```javascript
multiViewResover : {
		type : "org.nutz.plugins.view.MultiViewResover",
		fields : {
            defaultView : "ftl",// 默认视图 这里填前缀标识
			config : {//视图的属性文件配置
				refer : "conf"
			},
			resolvers : {
				"ftl" : {// 视图前缀标识
					refer : "freemarker"
				}
			}
		}
	}
```



Java

```java
At("/freemarker")
@IocBean
public class FreemarkerModule {
	@At
	@Ok("ftl:index")
	public void index() {

	}
    
    @At
	@Ok("ftl:test.index")
	public void index() {

	}
} 
```



#### JetTemplate

```xml
<dependency>
	<groupId>com.github.subchen</groupId>
		<artifactId>jetbrick-template</artifactId>
		<version>2.1.8</version>
	</dependency>
<dependency>
<groupId>com.github.subchen</groupId>
	<artifactId>jetbrick-template-web</artifactId>
	<version>2.1.8</version>
</dependency
```

假如模板页面所在路径是/WEB-INF/templates/jetTemplate,有如下页面：

1. index.html
2. test/index.html

注：jetbrick-template.properties里配置的路径被view.js配置的路径给覆盖，及如下配置无效

```shell
template.loader = $loader
$loader = jetbrick.template.loader.ResourceLoader
$loader.root = /WEB-INF
$loader.reloadable =false
```

json配置

```javascript
jetTemplate : {
	type : "org.nutz.plugins.view.JetTemplateView",
	args : [ null ],
	fields : {
		prefix : "/WEB-INF/templates/jetTemplate",
		suffix : ".html"
	}
}
```

配置multiViewResover：

```javascript
multiViewResover : {
		type : "org.nutz.plugins.view.MultiViewResover",
		fields : {
            defaultView : "jetx",// 默认视图 这里填前缀标识
			config : {//视图的属性文件配置
				refer : "conf"
			},
			resolvers : {
				"jetx" : {// 视图前缀标识
					refer : "jetTemplate"
				}
			}
		}
	}
```



java

```java
@At("/jetTemplate")
@IocBean
public class JetTemplateModule {
	@At
	@Ok("jetx:index")
	public void index() {

	}
    
    @At
	@Ok("jetx:test.index")
	public void index() {

	}
}
```



#### Captcha

```javascript
	captcha : {
		type : "org.nutz.plugins.view.CaptchaView",
		args : [ null ]
	}
```

配置multiViewResover：

```javascript
multiViewResover : {
		type : "org.nutz.plugins.view.MultiViewResover",
		fields : {
			config : {//视图的属性文件配置
				refer : "conf"
			},
			resolvers : {
				"cap" : {// 视图前缀标识
					refer : "captcha"
				}
			}
		}
	}
```

Java

```java

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;

@IocBean
@At("/captcha")
public class CaptchaModule {
	@At
	@Ok("cap")
	public void get() {
		
	}
}
```





#### Velocity

```xml
<dependency>
	<groupId>org.apache.velocity</groupId>
	<artifactId>velocity</artifactId>
	<version>1.7</version>
</dependency>
<dependency>
	<groupId>org.apache.velocity</groupId>
	<artifactId>velocity-tools</artifactId>
	<version>2.0</version>
</dependency>
```

#### Jsp

json配置

```javascript
jsp : {
	type : "org.nutz.plugins.view.JspView",
	args : [ null ],
	fields : {
		prefix : "/WEB-INF/templates/jsp",
		uffix : ".jsp"
	}
}
```

配置multiViewResover：

```javascript
multiViewResover : {
		type : "org.nutz.plugins.view.MultiViewResover",
		fields : {
			defaultView : "jsp",// 默认视图 这里填前缀标识
			config : {//视图的属性文件配置
				refer : "conf"
			},
			resolvers : {
				"jsp" : {// 视图前缀标识
					refer : "jsp"
				}
			}
		}
	}
```



java

```java
@At("/jsp")
@IocBean
public class JetTemplateModule {
	@At
	@Ok("jsp:index")
	public void index() {

	}
    
    @At
	@Ok("jsp:test.index")
	public void index() {

	}
}
```



#### 完整配置示例

```javascript
var ioc = {
	conf : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : [ "custom/" ]
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
	java8TimeDialect : {
		type : "org.thymeleaf.extras.java8time.dialect.Java8TimeDialect"
	},
	thymeleaf : {
		type : "org.nutz.plugins.view.ThymeleafView",
		args : [ null ],
		fields : {
			prefix : "/WEB-INF/templates/thymeleaf",
			suffix : ".html",
			contentType : "text/html",
			encoding : "UTF-8",
			properties : {
				type : "org.nutz.lang.util.NutMap",
				args : [ {//动态可扩展属性配置
					"templateMode" : "HTML5",
					"cacheable" : true,
					"cacheTTLMs" : 3600000,
					"dialects" : {
						refer : "java8TimeDialect"
					} 
				} ]// //可配置多个插件引用用逗号分隔
			}
		}
	},
    captcha : {
		type : "org.nutz.plugins.view.CaptchaView",
		args : [ null ]
	},
    jsp : {
        type : "org.nutz.plugins.view.JspView",
        args : [ null ],
        fields : {
            prefix : "/WEB-INF/templates/jsp",
            uffix : ".jsp"
        }
    },
	multiViewResover : {
		type : "org.nutz.plugins.view.MultiViewResover",
		fields : {
			defaultView : "btl",// 默认视图 这里填前缀标识
			config : {//视图的属性文件配置
				refer : "conf"
			},
			resolvers : {
				"btl" : {// 视图前缀标识
					refer : "beetl"
				},
				"th" : {// 视图前缀标识
					refer : "thymeleaf"
				},
				"ftl" : {// 视图前缀标识
					refer : "freemarker"
				},
				"jetx" : {// 视图前缀标识
					refer : "jetTemplate"
				},
                "jsp" : {// 视图前缀标识
					refer : "jsp"
				},
				"cap":{
					refer:"captcha"
				}
			}
		}
	}
}
```

访问相应的链接，就会找到相应的视图。

例如：http://localhost:8080/mutliview/beetl/test/index 

此时插件会找/WEB-INFO/templates/beetl/test/index.html页面



#### 默认视图使用示例

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

注意点：

1. `在使用此功能时，请把MainModuled的@Ok("json")注解去除。`

2. `defaultView如果没有配置，则默认按顺序找第一个配置的视图模板引擎。`

3. `默认视图不会跟Nutz里内置支持的视图冲突，如以下是MultiView的常量：`

   ```java
   String INNER_VIEW_TYPE = "json|raw|re|void|http|redirect|forward|>>|->";
   ```

   

##### 支持配置的模板路径以外访问方式

假如配置了模板路径/WEB-INF/templates/beetl，并且默认视图也是beetl

```
@At("/my/cbd")
public void someFunc(){
}

@At("btl:/my/abc")
public void abc(){
}
```

如上示例，则以上两个请求会在项目根路径寻找模板页面：

1. /my/cbd.html
2. /my/abc.html

而不会去配置好的模板路径去找，例：/WEB-INF/templates/beetl/my/cbd.html

#### session和application切换视图示例

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
   /**
	 * session切换主题
	 */
	@At("/session/change")
	@Ok("index")
	public void sessioinChange() {
		HttpSession session = Mvcs.getHttpSession();
		session.setAttribute(MultiView.DEFAULT_VIEW, "ftl");
		session.setAttribute(MultiView.VIEW_PREFIX, "/templates/freemarker");
		session.setAttribute(MultiView.DEFAULT_SUFFIX, ".html");
	}

	/**
	 * application切换主题
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

### 内置的变量:

| 变量名           | 描述                                                         |
| ---------------- | ------------------------------------------------------------ |
| path             | 项目根路径                                                   |
| basePath         | 完整的项目连接路径                                           |
| tplDir           | 模板所在目录                                                 |
| props            | 当前系统环境                                                 |
| msgs             | 国际语言                                                     |
| cfg              | 配置文件                                                     |
| viewName         | 用于显示使用的模板视图的名称                                 |
| resPath          | 资源根路径，需在属性配置文件中配置resource.dir变量的值       |
| tplResPath       | 模板对应的资源路径                                           |
| servletExtension | 请求连接的后缀，需在属性配置文件中配置servlet.extension变量的值 |

这些变量对更换模板功能提供了便利，可在页面中调用以上变量。

# 使用示例

假如有两套主题皮肤的模板页面所在路径：

1. WEB-INFO/templates/sun
2. WEB-INFO/templates/moon

静态资源所在项目根路径：

1. 公有：resources/lib
2. sun主题: resources/sun/lib
3. moon主题: resources/moon/lib

属性文件中配置：

```shell
# If you change this value, is necessary to edit WEB-INF/web.xml as well
servlet.extension=.nt
resource.dir=resources
```

页面中引用变量示例：

css

```html
<link rel="stylesheet" href="${resPath}lib/jquery-ui/jquery-ui.css" />
<link rel="stylesheet" href="${tplResPath}lib/_mod/jquery-ui/jquery-ui.css" />
```

链接

```html
<a href="${basePath}test/index${servletExtension}">test</a>
```

假如sun路径的模板页面后缀类型为html，sun路径的模板页面后缀类型为ftl，默认配置了模板路径为sun，配置代码片段如下：

```
prefix : "/WEB-INF/templates/sun",
suffix : ".html",
```

则切换moon主题时，只要改以上两个配置项：

```
prefix : "/WEB-INF/templates/moon",
suffix : ".ftl",
```

就能实现切换主题皮肤效果了，而不用动源码，包括链接扩展名和资源路径，也只要改配置文件。

```shell
# If you change this value, is necessary to edit WEB-INF/web.xml as well
servlet.extension=.html
#静态资源地址
resource.dir=http://sta.dhf.ink/
```



# 插件的核心类

| 类名                         | 描述                                                     |
| ---------------------------- | -------------------------------------------------------- |
| ResourceBundleViewResolver   | 实现的MultiView接口，用于从 IOC 容器配置文件中查找视图。 |
| AbstractTemplateViewResolver | 抽象出通用的视图请求操作，填充全局变量，资源路径计算     |
| MultiViewResover             | 主要用于注入多视图，设置默认视图，配置文件               |
| MultiView                    | MultiView继承ViewMaker2接口，主要在此接口里定义常量      |

`注：MultiViewResover增强了灵活性。如上示例view.js配置文件中，multiViewResover不再是约定的名称，只要定义 org.nutz.plugins.view.MultiViewResover类型，插件都能加载，可灵活定义多个MultiViewResover，插件最最终会进行组合去重，后来者覆盖前者配置`。

# 实现扩展

如果想添加别的视图，只需继承于AbstractTemplateViewResolver，实现init和render方法即可。
例如以下是在此插件下beetl模板视图的实现



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

*See [BeetlView.java](https://github.com/nutzam/nutzmore/blob/master/nutz-plugins-multiview/src/main/java/org/nutz/plugins/view/BeetlView.java) on GitHub.*

注意上面的getConfigPath()方法的代码，是为了实现beetl放在公共的lib目录获取不到beetl配置文件的问题而补救的一个解决方案，configPath是父类AbstractTemplateViewResolver 的属性,可在ioc配置文件中配置。 
init方法只执行一次，一般用于加载视图的配置相关的代码，且某些对象只需实例化一次，后面就不用实例化。
render方法的sharedVars是全局的变量。

# 未来规划

1. 支持全局变量的添加，例如，把一些字典的类加载到全局里，供页面调用。

2. 支持Ajax视图，此需要特殊处理。例如，前段Ajax请求，需要处理获取的数据、本次请求状态、提示信息等。

3. ###### 增加对pdf、velocity视图支持，增强Freemarker视图可配置性。 