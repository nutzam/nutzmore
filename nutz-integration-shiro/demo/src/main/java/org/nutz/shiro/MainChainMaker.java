package org.nutz.shiro;

import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.aop.AuthorizingAnnotationMethodInterceptor;
import org.nutz.integration.shiro.NutShiroProcessor;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChain;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Processor;
import org.nutz.mvc.impl.NutActionChain;
import org.nutz.mvc.impl.processor.ActionFiltersProcessor;
import org.nutz.mvc.impl.processor.AdaptorProcessor;
import org.nutz.mvc.impl.processor.EncodingProcessor;
import org.nutz.mvc.impl.processor.FailProcessor;
import org.nutz.mvc.impl.processor.MethodInvokeProcessor;
import org.nutz.mvc.impl.processor.ModuleProcessor;
import org.nutz.mvc.impl.processor.UpdateRequestAttributesProcessor;
import org.nutz.mvc.impl.processor.ViewProcessor;
import org.nutz.shiro.ext.anno.ThunderRequiresPermissions;
import org.nutz.shiro.ext.anno.ThunderRequiresRoles;
import org.nutz.shiro.ext.aop.ThunderPermissionAnnotationMethodInterceptor;
import org.nutz.shiro.ext.aop.ThunderRoleAnnotationMethodInterceptor;

/**
 * 
 * @author kerbores
 *
 * @email kerbores@gmail.com
 *
 */
public class MainChainMaker implements ActionChainMaker {

	private Log log = Logs.get();

	@Override
	public ActionChain eval(final NutConfig config, final ActionInfo ai) {
		List<Processor> list = normalList();

		List<AuthorizingAnnotationMethodInterceptor> interceptors = new ArrayList<AuthorizingAnnotationMethodInterceptor>();

		interceptors.add(new ThunderPermissionAnnotationMethodInterceptor());
		interceptors.add(new ThunderRoleAnnotationMethodInterceptor());

		addBefore(list, ActionFiltersProcessor.class, new NutShiroProcessor(interceptors, ThunderRequiresPermissions.class, ThunderRequiresRoles.class));

		Processor error = new FailProcessor();
		Lang.each(list, new Each<Processor>() {

			@Override
			public void invoke(int paramInt1, Processor processor, int paramInt2) throws ExitLoop, ContinueLoop, LoopException {
				try {
					processor.init(config, ai);
				} catch (Throwable e) {
					log.error(e);
				}
			}
		});
		try {
			error.init(config, ai);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return new NutActionChain(list, error, ai);
	}

	protected List<Processor> normalList() {
		List<Processor> list = new ArrayList<Processor>();
		list.add(new UpdateRequestAttributesProcessor());
		list.add(new EncodingProcessor());
		list.add(new ModuleProcessor());
		list.add(new ActionFiltersProcessor());
		list.add(new AdaptorProcessor());
		list.add(new MethodInvokeProcessor());
		list.add(new ViewProcessor());
		return list;
	}

	protected List<Processor> addBefore(List<Processor> list, Class<?> klass, Processor processor) {
		for (int i = 0; i < list.size(); i++) {
			if (klass.isAssignableFrom(list.get(i).getClass())) {
				list.add(i, processor);
				return list;
			}
		}
		return list;
	}
}
