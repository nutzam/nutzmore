package $package;

import org.nutz.integration.shiro.ShiroSessionProvider;
import org.nutz.mvc.annotation.ChainBy;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.LoadingBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.SessionBy;
import org.nutz.mvc.annotation.SetupBy;
import org.nutz.mvc.annotation.UrlMappingBy;
import org.nutz.mvc.annotation.Views;
import org.beetl.ext.nutz.BeetlViewMaker;


@SetupBy(value = MainSetup.class)
@IocBy(args = {"*js",
               "ioc/",
               "*anno",
               "$package",
               "*quartz", // 关联Quartz
               "*async", "128",
               "*tx",
               "*jedis",
               "*slog"})
@Modules(scanPackage = true)
@ChainBy(args = "mvc/nutzbook-mvc-chain.js")
@Ok("json:full")
@Fail("jsp:jsp.500")
@Localization(value = "msg/", defaultLocalizationKey = "zh-CN")
@Views({BeetlViewMaker.class})
@SessionBy(ShiroSessionProvider.class)
public class MainModule {}
