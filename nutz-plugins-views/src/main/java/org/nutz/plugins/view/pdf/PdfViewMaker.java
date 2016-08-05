package org.nutz.plugins.view.pdf;

import org.nutz.ioc.Ioc;
import org.nutz.mvc.View;
import org.nutz.mvc.ViewMaker;

public class PdfViewMaker implements ViewMaker {

    public View make(Ioc ioc, String type, String value) {
        if ("pdf".equalsIgnoreCase(type)) {
            return new PdfView(value);
        }
        return null;
    }

}
