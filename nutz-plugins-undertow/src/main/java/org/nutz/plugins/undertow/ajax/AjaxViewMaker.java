package org.nutz.plugins.undertow.ajax;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

/**
 * 可以在ioc中配置一个叫做ajaxView的bean来设置js的格式
 * 
 * @author pw
 */
public class AjaxViewMaker implements ViewMaker {

    private boolean useIoc = false;

    private boolean firstMake = true;

    @Override
    public View make(Ioc ioc, String type, String value) {
        if ("ajax".equalsIgnoreCase(type)) {
            // 第一次的时候做一下尝试，看看ioc里面有没有配置一个叫ajaxView的东东^_^
            if (firstMake) {
                try {
                    AjaxView av = ioc.get(AjaxView.class, "ajaxView");
                    if (av != null) {
                        useIoc = true;
                        return av;
                    }
                }
                catch (Exception e) {
                    useIoc = false;
                }
                finally {
                    firstMake = false;
                }
            }
            return useIoc ? ioc.get(AjaxView.class, "ajaxView") : new AjaxView();
        }
        return null;
    }

}
