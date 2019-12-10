package org.nutz.plugins.validation;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.plugins.validation.annotation.AnnotationValidation;

import java.util.List;

/**
 * 可用于 MVC 效验的动作链
 * <p>
 * 要求 action 参数中必须有一个 Errors 类型的参数（不需要使用 Param 声明）。当验证完成后会向这个参数赋值
 *
 * @author QinerG(QinerG@gmail.com)
 * @author wendal(wendal1985@gmail.com)
 * @author threefish(306955302@gmail.com)
 */
public class ValidationProcessor extends AbstractProcessor {

    private static AnnotationValidation av = new AnnotationValidation();

    protected int index = -1;

    @Override
    public void init(NutConfig config, ActionInfo ai) throws Throwable {
        Class<?>[] tmp = ai.getMethod().getParameterTypes();
        int len = tmp.length;
        for (int i = 0; i < len; i++) {
            if (tmp[i].isAssignableFrom(Errors.class)) {
                index = i;
            }
        }
    }

    @Override
    public void process(ActionContext ac) throws Throwable {
        if (index >= 0) {
            Errors es = new Errors();
            for (Object obj : ac.getMethodArgs()) {
                if (obj instanceof Object[]) {
                    Object[] objects = (Object[]) obj;
                    for (Object object : objects) {
                        av.validate(object, es);
                    }
                } else if (obj instanceof List) {
                    List objects = (List) obj;
                    for (Object object : objects) {
                        av.validate(object, es);
                    }
                } else {
                    av.validate(obj, es);
                }
            }
            ac.getMethodArgs()[index] = es;
        }
        doNext(ac);
    }
}
