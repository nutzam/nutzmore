package org.nutz.protobuf;

import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.IocBy;
import org.nutz.mvc.annotation.Modules;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Views;
import org.nutz.mvc.ioc.provider.ComboIocProvider;
import org.nutz.protobuf.mvc.view.ProtoViewMaker;

@IocBy(type = ComboIocProvider.class, args = { "*js", "ioc/", "*anno", "org.nutz.protobuf" })
@Modules(scanPackage = true)
@Ok("json:full")
@Fail("jsp:jsp.500")
@Views({ ProtoViewMaker.class })
public class MainModule {
}
