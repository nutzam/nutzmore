package org.nutz.mvc.testapp.classes;

import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Localization;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.plugins.view.ResourceBundleViewResolver;

@Modules(scanPackage=true)
@Ok("json")
@Fail("json")
@IocBy(type=ComboIocProvider.class,
        args={"*org.nutz.ioc.loader.json.JsonLoader","org/nutz/mvc/testapp/classes/ioc","ioc/",
              "*org.nutz.ioc.loader.annotation.AnnotationIocLoader","org.nutz.mvc.testapp.classes",
              "*tx"})
@Localization("org/nutz/mvc/testapp/classes/message/")
@Views({ResourceBundleViewResolver.class})
public class MainModule {
}
