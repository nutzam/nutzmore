package org.nutz.plugins.view.freemarker;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

public class FreemarkerViewMaker implements ViewMaker {

    protected FreeMarkerConfigurer freeMarkerConfigurer;
    
    protected String iocName = "freeMarkerConfigurer";
    
	public View make(Ioc ioc, String type, String value) {
		if ("fm".equalsIgnoreCase(type) || "ftl".equalsIgnoreCase(type)) {
		    if (freeMarkerConfigurer == null) {
		        for (String name : ioc.getNames()) {
                    if (iocName.equals(name)) {
                        freeMarkerConfigurer = ioc.get(FreeMarkerConfigurer.class);
                        break;
                    }
                }
		        if (freeMarkerConfigurer == null) {
		            freeMarkerConfigurer = new FreeMarkerConfigurer();
		            freeMarkerConfigurer.init();
		        }
		    }
			return new FreemarkerView(freeMarkerConfigurer, value);
		}
		return null;
	}

}
