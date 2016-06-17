package org.nutz.plugins.view.thymeleaf;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

public class ThymeleafViewMaker implements ViewMaker {

    protected ThymeleafProperties properties;

    protected String iocName = "thymeleafProperties";

    public View make(Ioc ioc, String type, String value) {
        if ("th".equalsIgnoreCase(type)) {
            if (properties == null) {
                for (String name : ioc.getNames()) {
                    if (iocName.equals(name)) {
                        properties = ioc.get(ThymeleafProperties.class);
                        break;
                    }
                }
                if (properties == null) {
                    properties = new ThymeleafProperties();
                }
            }
            return new ThymeleafView(properties, value);
        }
        return null;
    }
}
