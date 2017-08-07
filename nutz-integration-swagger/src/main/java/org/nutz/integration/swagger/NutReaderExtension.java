package org.nutz.integration.swagger;

import java.lang.reflect.Method;

import org.nutz.lang.Strings;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.PUT;

import io.swagger.annotations.Api;
import io.swagger.servlet.ReaderContext;
import io.swagger.servlet.extensions.ServletReaderExtension;

public class NutReaderExtension extends ServletReaderExtension {

    public int getPriority() {
        return 1;
    }

    public boolean isReadable(ReaderContext context) {
        final Api apiAnnotation = context.getCls().getAnnotation(Api.class);
        return apiAnnotation != null && (context.isReadHidden() || !apiAnnotation.hidden());
    }

    public String getHttpMethod(ReaderContext context, Method method) {
        At at = method.getAnnotation(At.class);
        if (at == null)
            return null;
        if (method.getAnnotation(GET.class) != null)
            return "GET";
        if (method.getAnnotation(POST.class) != null)
            return "POST";
        if (method.getAnnotation(PUT.class) != null)
            return "PUT";
        if (method.getAnnotation(DELETE.class) != null)
            return "DELETE";
        if (at.methods().length > 0)
            return at.methods()[0];
        return null;
    }

    public String getPath(ReaderContext context, Method method) {
        At at = method.getAnnotation(At.class);
        if (at == null)
            return null;
        At clsAt = context.getCls().getAnnotation(At.class);
        if (clsAt != null) {
            if (clsAt.value().length == 0) {
                context.setParentPath("/" + Strings.lowerFirst(context.getCls().getSimpleName()));
            } else {
                context.setParentPath(clsAt.value()[0]);
            }
        } else {
            context.setParentPath("");
        }
        if (at.value().length > 0) {
            return context.getParentPath() + at.value()[0];
        }
        return context.getParentPath() + "/" + method.getName();
    }

}
