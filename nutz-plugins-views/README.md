# nutzmore Nutz的插件与扩展


简介(可用性:生产)
==================================

freemarker/velocity/thymeleaf/pdf 视图插件

## freemarker 视图使用方法：

1. 在 MainModule 的 `@IocBy` 中增加 "*org.nutz.plugins.view.freemarker.FreemarkerIocLoader"
1. 在 MainModule 中增加 `@Views(FreemarkerViewMaker.class)`
1. 如需自定义模板相关内容，请复制一份 freemarker.js 到/ioc 目录下，并修改相应内容

```js
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

```js
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

## thymeleaf 视图使用方法（现已支持 `thymeleaf 3.0.9.RELEASE` 版本，并自带 `thymeleaf-layout-dialect 2.3.0`）：

1. 在 MainModule 的 `@IocBy` 中增加 "*org.nutz.plugins.view.freemarker.ThymeleafIocLoader"
2. 在 MainModule 中增加 `@Views(ThymeleafViewMaker.class)`
3. 在 `@Ok` 注解中用 `th` 表示使用 thymeleaf 视图渲染，如 `@Ok("th:home/index")`
4. 如需自定义模板相关内容，请创建一份 thymeleaf.js 到/ioc 目录下，并修改 `thymeleafProperties.fields` 中的相应内容

```js
var ioc = {
    thymeleafProperties: {
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
    java8TimeDialect: { type: "org.thymeleaf.extras.java8time.dialect.Java8TimeDialect" },
    thymeleafProperties: {
        type: "org.nutz.plugins.view.thymeleaf.ThymeleafProperties",
        fields: {
            dialects: [ { refer: "java8TimeDialect" } ]
        }
    }
};
```

## velocity视图集成方法
+  添加依赖

``` xml?linenums
<dependency>
	<groupId>org.nutz</groupId>
	<artifactId>nutz-plugins-views</artifactId>
	<version>${nutz.plugins.version}</version>
</dependency>
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

+ 主模块配置

```java?linenums
@Views({ VelocityViewMaker.class })
public class MainModule {

}
```

+ classpath配置

在classpath下增加 velocity.properties内容如下:

```java?linenums
#资源加载器或加载器别名
resource.loader = webapp
#资源加载器类全限定名    
webapp.resource.loader.class = org.apache.velocity.tools.view.WebappResourceLoader  
#资源位置
webapp.resource.loader.path=/WEB-INF/
#编码
input.encoding=UTF-8  
output.encoding=UTF-8 
#布局文件为准 
tools.view.servlet.layout.directory = layout/
#默认布局文件名称
tools.view.servlet.layout.default.template =default.html
#默认错误文件名称
tools.view.servlet.error.template =Error.vm
tools.view.servlet.layout.default.template =Default.vm
```

+ web.xml配置

nutz的filter或者servlet加上初始化参数
``` xml?linenums
<init-param>
	<param-name>org.apache.velocity.properties</param-name><!-- 这个不能修改-->
	<param-value>velocity.properties</param-value> <!-- 对应上一步中配置文件的位置 -->
</init-param>
```

+ 使用方法

User类
```java?linenums
public class User {
    public int roleId;
    public String userName;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
```

MVC类
``` java?linenums
@At("/")
@Ok("vm:/tmpl/main.vm")
public User main() {
    User user = new User();
    user.setUserName("nutz");
    user.setRoleId(0);
    return user;
}
```

main.vm文件
``` html?linenums
#if($!{obj.roleId} == 0)
<li> 管理员 $!{obj.userName}</li>
#else
<li> 编辑 $!{obj.userName}</li>
#end
```
+ 更灵活的使用方法

MVC类
```java?linenums
@At("/")
@Ok("vm:/tmpl/main.vm")
public NutMap main() {
    NutMap map = new NutMap();
    map.put("site_name", "Nutz工具箱");
    User user = new User();
    user.setRoleId(0);
    user.setUserName("nutz");
    map.put("user", user);
    return map;
}
```

main.vm文件
```html?linenums
<span> 站点名称：$!{obj.site_name}</span>
#if($!{obj.user.roleId} == 0)
<li> 管理员 $!{obj.user.userName}</li>
#else
<li> 编辑 $!{obj.user.userName}</li>
#end
```

+ 扩展工具

  - 实现一个工具类

   ``` java?linenums
      package com.tdb.utils;

      import org.apache.velocity.tools.config.DefaultKey;
      import org.apache.velocity.tools.config.InvalidScope;

      /**
       * author Jiangkun
       * created on 2016年5月22日
       */
      @DefaultKey("C")
      @InvalidScope({ "application" })
      public class CommonUtils {

          public static final int HIDDEN_LENGTH = 7;
          public static final String HIDDEN_IDENTIFER = "*";

          /**
           * 对商户端隐藏券号码
           * eg: 0004123456789012 -> 0004*******012
           * @param ticketno
           * @return
           */
          public static String hideTicketNo(String ticketno) {
              StringBuffer sb = new StringBuffer();
              String prefix = ticketno.substring(0, 4);
              String suffix = ticketno.substring(4+HIDDEN_LENGTH);
              sb.append(prefix);
              for(int i=0;i<HIDDEN_LENGTH;i++)
                  sb.append(HIDDEN_IDENTIFER);
              sb.append(suffix);
              return sb.toString();
          }
      }
     ```
   
  - 在WEB-INF目录添加tools.xml配置:

``` xml?linenums
<?xml version="1.0" encoding="UTF-8"?>
<tools>
    <data type="boolean" key="VIEW_TOOLS_AVAILABLE" value="true"/>
    <toolbox scope="request">
        <tool class="org.apache.velocity.tools.view.CookieTool"/>
        <tool class="org.apache.velocity.tools.view.ImportTool"/>
        <tool class="org.apache.velocity.tools.view.IncludeTool"/>
        <tool class="org.apache.velocity.tools.view.LinkTool"/>
        <tool class="org.apache.velocity.tools.view.ParameterTool"/>
        <tool class="org.apache.velocity.tools.view.ViewContextTool"/>
        <tool class="org.apache.velocity.tools.generic.ResourceTool"/>
        <tool class="org.apache.velocity.tools.generic.DateTool"/>
        <tool class="org.apache.velocity.tools.generic.MathTool"/>
        <tool class="com.tdb.boss.tools.GlobalUtils"/>
        <tool class="com.tdb.boss.tools.MenuUtils"/>
        <tool class="com.tdb.boss.tools.SessionUtils"/>
        <tool class="com.tdb.utils.CommonUtils"/>
    </toolbox>
    <toolbox scope="session" createSession="false">
        <tool class="org.apache.velocity.tools.view.BrowserTool"/>
    </toolbox>
</tools>
```

## 验证码视图

请查验 http://nutz-captcha.mydoc.io

