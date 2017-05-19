package org.nutz.plugins.zdoc.html;

import org.nutz.plugins.zdoc.NutDSet;

/**
 * 封装所有的和 html 相关的操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class Htmls {

    public static void renderDSet(NutDSet ds, String target) {
        LocalHtmlDSetRender r = new LocalHtmlDSetRender();
        r.render(ds, target);
    }

}
