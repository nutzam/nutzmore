package org.nutz.plugins.zdoc.msword;

import org.nutz.plugins.zdoc.NutDSet;

/**
 * 封装所有的和 MS-WORD 相关的操作
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class MsWords {

    public static void renderDSet(NutDSet ds, String target) {
        AbstractMsWordDSetRender r = new LocalMsWordDSetRender();
        r.render(ds, target);
    }

}
