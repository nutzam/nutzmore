package org.nutz.integration.authz;

import org.nutz.mvc.annotation.*;

/**
 * 测试模块
 * @author Yang Luo<hsluoyz@gmail.com>
 *
 */
@Filters({@By(type=JCasbinAuthzFilter.class), @By(type=HttpBasicAuthnFilter.class)})
public class DemoModule {
    @At("/*")
    @Ok("json")
    public String index() {
        return "200 OK";
    }
}
