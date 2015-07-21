package org.nutz.integration.jsr303;

import java.lang.annotation.Annotation;

import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.impl.processor.AbstractProcessor;

/**
 * 实现jsr303的Processor,实现对入口方法参数的校验, 需要参数声明@Valid注解,及声明一个ValidationResult参数.<p/>
 * @author wendal(wendal1985@gmail.com)
 *
 */
public class ValidationProcessor extends AbstractProcessor {

	protected static ValidatorFactory factory;

	protected Validator validator;

	protected Valid[] valids;
	
	protected int reIndex = -1;

	public ValidationProcessor() {
		init();
	}

	/**
	 * 建议子类覆盖这个方法以最大化定制validator
	 */
	public void init() {
	    if (factory == null) {
	        factory = Validation.buildDefaultValidatorFactory();
	    }
		validator = factory.getValidator();
	}

	public void init(NutConfig config, ActionInfo ai) throws Throwable {
	    Annotation[][] annss = ai.getMethod().getParameterAnnotations();
		if (annss.length == 0)
		    return;
		Valid[] valids = new Valid[annss.length];
		for (int i = 0; i < annss.length; i++) {
		    Annotation[] anns = annss[i];
		    for (Annotation ann : anns) {
                if (ann.equals(Valid.class)) {
                    valids[i] = (Valid) ann;
                    break;
                }
            }
        }
		Class<?>[] ks = ai.getMethod().getParameterTypes();
		for (int i = 0; i < ks.length; i++) {
            if (ks[i].isAssignableFrom(ValidationResult.class)) {
                reIndex = i;
                this.valids = valids;
            }
        }
	}

	public void process(ActionContext ac) throws Throwable {
		Object[] args = ac.getMethodArgs();
		if (reIndex > -1) {
            ValidationResult vr = new ValidationResult();
		    for (int i = 0; i < args.length; i++) {
                if (args[i] != null && valids[i] != null) {
                    vr.add(validator.validate(args[i]));
                } else {
                    vr.add(null);
                }
            }
		    args[reIndex] = vr;
		}
		doNext(ac);
	}

}
