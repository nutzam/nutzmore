package org.nutz.plugins.apidoc;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.JsonFormat;
import org.nutz.lang.ContinueLoop;
import org.nutz.lang.Each;
import org.nutz.lang.Encoding;
import org.nutz.lang.ExitLoop;
import org.nutz.lang.Lang;
import org.nutz.lang.LoopException;
import org.nutz.lang.Mirror;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.Times;
import org.nutz.lang.util.ClassMeta;
import org.nutz.lang.util.ClassMetaReader;
import org.nutz.lang.util.NutMap;
import org.nutz.lang.util.SimpleContext;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.ActionChainMaker;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.ActionInfo;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.ObjectInfo;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.impl.ActionInvoker;
import org.nutz.mvc.impl.UrlMappingImpl;
import org.nutz.mvc.view.RawView;
import org.nutz.mvc.view.UTF8JsonView;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;
import org.nutz.plugins.apidoc.annotation.ApiParam;
import org.nutz.plugins.apidoc.annotation.Manual;
import org.nutz.plugins.apidoc.annotation.ReturnKey;

/**
 * Api文档生成
 * 
 * @author wendal
 *
 */
public class ApidocUrlMapping extends UrlMappingImpl {

	/**
	 * 按类(或组?)分类排好的列表
	 */
	protected static LinkedHashMap<String, ExpClass> infos = new LinkedHashMap<>();

	protected NutMap projectInfo;

	private static final Log log = Logs.get();

	protected static String[] EMTRY = new String[0];

	protected String globalFailView;

	protected List<NutMap> defaultFails;

	{
		defaultFails = new ArrayList<NutMap>();
		defaultFails.add(NutMap.NEW().addv("key", 404).addv("description", "Not Found"));
		defaultFails.add(NutMap.NEW().addv("key", 403).addv("description", "Permission Denied"));
		defaultFails.add(NutMap.NEW().addv("key", 500).addv("description", "Exception Occured"));
	}

	@Override
	public void add(ActionChainMaker maker, ActionInfo ai, NutConfig nc) {
		super.add(maker, ai, nc);
		ExpContext ctx = new ExpContext();
		ctx.set("maker", maker);
		ctx.set("ai", ai);
		ctx.set("nc", nc);
		_add(ctx);

		if (projectInfo == null) {
			projectInfo = new NutMap();
			Fail fail = nc.getMainModule().getAnnotation(Fail.class);
			if (fail != null) {
				globalFailView = fail.value();
			}
			Manual document = nc.getMainModule().getAnnotation(Manual.class);
			if (document != null) {
				projectInfo.addv("name", document.name()).addv("description", document.description())
						.addv("author", document.author()).addv("email", document.email())
						.addv("homePage", document.homePage()).addv("copyright", document.copyRight());
			}
		}
	}

	@Override
	public ActionInvoker get(ActionContext ac) {
		// 如果是读取expPath,俺就自行处理了
		String path = Mvcs.getRequestPath(ac.getRequest());
		if (path.startsWith(expPath)) {
			if (path.equals(expPath) || path.equals(expPath + "index")) {
				HttpServletResponse resp = ac.getResponse();
				resp.setCharacterEncoding("UTF-8");
				resp.setContentType("text/html");
				InputStream ins = ac.getServletContext().getResourceAsStream(expPath + "index.html");
				if (ins != null) {
					String tmp = Streams.readAndClose(new InputStreamReader(ins, Encoding.CHARSET_UTF8));
					if (Strings.isBlank(tmp)) {
						ins = null;
					} else {
						ins = ac.getServletContext().getResourceAsStream(expPath + "index.html");
					}
				}
				if (ins == null) {
					ins = getClass().getResourceAsStream("index.html");
				}
				try {
					new RawView("html").render(ac.getRequest(), resp, ins);
				} catch (Throwable e) {
					log.debug(e.getMessage(), e);
				}
				return new EmtryActionInvoker();
			} else if (path.equals(expPath + "exp")) {
				return docInvoker;
			}
		}
		return super.get(ac);
	}

	protected View view = new UTF8JsonView(JsonFormat.full());
	protected ActionInvoker docInvoker = new DocActionInvoker();
	protected String expPath = "/_/";
	protected ApiMatchMode baseMatchMode = ApiMatchMode.ONLY;

	class EmtryActionInvoker extends ActionInvoker {
		@Override
		public boolean invoke(ActionContext ac) {
			return true;
		}
	}

	class DocActionInvoker extends ActionInvoker {
		@Override
		public boolean invoke(ActionContext ac) {
			try {
				ac.getResponse().setCharacterEncoding("UTF-8");
				NutMap re = new NutMap("data", infos);
				re.put("content_path", ac.getRequest().getContextPath());
				re.put("project", projectInfo);
				view.render(ac.getRequest(), ac.getResponse(), re);
			} catch (Throwable e) {
				log.debug("exp fail", e);
			}
			return true;
		}
	}

	protected static class ExpClass extends NutMap {
		private static final long serialVersionUID = 1L;
	}

	protected static class ExpMethod extends NutMap {
		private static final long serialVersionUID = 1L;
	}

	/**
	 * 方法参数
	 * 
	 * @author wendal
	 *
	 */
	protected static class ExpParam extends NutMap {
		private static final long serialVersionUID = 1L;
	}

	protected static class ExpContext extends SimpleContext {
		public NutConfig nc() {
			return getAs(NutConfig.class, "nc");
		}

		public ExpClass expClass() {
			return getAs(ExpClass.class, "expClass");
		}

		public ExpMethod expMethod() {
			return getAs(ExpMethod.class, "expMethod");
		}

		public ActionInfo ai() {
			return getAs(ActionInfo.class, "ai");
		}
	}

	@SuppressWarnings("unchecked")
	protected ExpMethod _add(ExpContext ctx) {
		String typeName = ctx.ai().getModuleType().getName();
		ExpClass expClass = infos.get(typeName);
		if (expClass == null) {
			expClass = makeClass(ctx.ai().getModuleType(), ctx);
			if (expClass == null) {
				log.trace("skip null ExpClass");
				return null;
			}
			infos.put(typeName, expClass);
		}
		ctx.set("expClass", expClass);
		List<ExpMethod> methods = expClass.getAs("methods", List.class);
		if (methods == null) {
			methods = new ArrayList<>();
			expClass.put("methods", methods);
		}
		Api api = ctx.ai().getMethod().getAnnotation(Api.class);
		if (api == null && expClass.getAs("apiMatchMode", ApiMatchMode.class) == ApiMatchMode.ONLY) {
			log.trace("skip null @Api Method");
			return null;
		}
		ExpMethod expMethod = makeMethod(ctx);
		if (expMethod == null) {
			log.trace("skip null ExpMethod");
			return null;
		}
		methods.add(expMethod);
		return expMethod;
	}

	protected ExpClass makeClass(Class<?> klass, ExpContext ctx) {
		Api api = klass.getAnnotation(Api.class);
		if (api == null && baseMatchMode != ApiMatchMode.ALL)
			return null;
		if (api != null && api.match() == ApiMatchMode.NONE)
			return null;
		ExpClass expClass = new ExpClass();
		expClass.put("typeName", klass.getName());
		IocBean ib = klass.getAnnotation(IocBean.class);
		if (ib != null)
			expClass.put("iocName", Strings.isBlank(ib.name()) ? Strings.lowerFirst(klass.getSimpleName()) : ib.name());
		if (api != null) {
			expClass.put("name", api.name());
			expClass.put("description", api.description());
			expClass.put("apiMatchMode", api.match());
		}
		if (Strings.isBlank(expClass.getString("name")))
			expClass.put("name", klass.getSimpleName());
		At at = klass.getAnnotation(At.class);
		expClass.put("pathPrefixs", at == null ? new String[0] : at.value());
		InputStream ins = klass.getClassLoader().getResourceAsStream(klass.getName().replace(".", "/") + ".class");
		if (ins != null) {
			try {
				ClassMeta meta = ClassMetaReader.build(ins);
				expClass.setv("meta", meta);
			} catch (Exception e) {
			}
		}
		return expClass;
	}

	protected NutMap cache = NutMap.NEW();

	/**
	 * 实例化一个0值对象
	 * 
	 * @param clazz
	 * @return
	 */
	protected Object instance(Class clazz) {
		if (cache.get(clazz.getName()) != null) {
			return cache.get(clazz.getName());
		}
		if (clazz == int.class || clazz == Integer.class) {
			return 0;
		}
		if (clazz == short.class || clazz == Short.class) {
			return 0;
		}
		if (clazz == double.class || clazz == Double.class) {
			return 0.0d;
		}
		if (clazz == byte.class || clazz == Byte.class) {
			return 0;
		}
		if (clazz == long.class || clazz == Long.class) {
			return 0l;
		}
		if (clazz == float.class || clazz == Float.class) {
			return 0.0f;
		}
		if (clazz == char.class || clazz == Character.class) {
			return 'a';
		}
		if (clazz == boolean.class || clazz == Boolean.class) {
			return false;
		}
		if (clazz == String.class) {
			return "S";
		}
		if (clazz == Date.class) {
			return Times.now();
		}
		if (clazz == BigDecimal.class) {
			return new BigDecimal(0);
		}
		if (clazz == BigInteger.class) {
			return new BigInteger("1");
		}
		try {
			Object obj = clazz.newInstance();
            cache.put(clazz.getName(), obj);
			Field[] fields = Mirror.me(clazz).getFields();
			for (Field field : fields) {
				if (field.getType() != clazz) {
				    Object _obj = instance(field.getType());
				    cache.put(field.getType().getName(), _obj);
					Mirror.me(clazz).setValue(obj, field.getName(), _obj);
				}
			}
			return obj;
		} catch (InstantiationException | IllegalAccessException e) {
		}
		return null;
	}

	protected ExpMethod makeMethod(ExpContext ctx) {
		ActionInfo ai = ctx.ai();
		ExpMethod expMethod = new ExpMethod();
		ctx.set("expMethod", expMethod);
		expMethod.put("chainName", ai.getChainName() == null ? "default" : ai.getChainName());
		expMethod.put("typeName", ai.getModuleType().getName());
		expMethod.put("okView", ai.getOkView());
		expMethod.put("failView", Strings.isBlank(ai.getFailView()) ? globalFailView : ai.getFailView());
		
		expMethod.put("lineNumber", ai.getLineNumber());
		expMethod.put("paths", ai.getPaths());
		expMethod.put("returnType", ai.getMethod().getReturnType().getName());
		expMethod.put("returnData", instance(ai.getMethod().getReturnType()));
		expMethod.put("methodName", ai.getMethod().getName());
		if (ai.getAdaptorInfo() != null)
			expMethod.put("adaptorName", ai.getAdaptorInfo().getType().getName());
		expMethod.put("requestBody",
				Strings.equals(expMethod.getString("adaptorName"), "org.nutz.mvc.adaptor.JsonAdaptor"));
		expMethod.put("httpMethods", ai.getHttpMethods() == null || ai.getHttpMethods().size() == 0
				? Strings.equals(expMethod.getString("adaptorName"), "org.nutz.mvc.adaptor.JsonAdaptor") ?new String[] {  "POST" } : new String[] { "GET", "POST" }  : ai.getHttpMethods());
		ObjectInfo<? extends ActionFilter>[] filters = ai.getFilterInfos();
		if (filters == null)
			expMethod.put("filters", EMTRY);
		else {
			List<String> filterNames = new ArrayList<>();
			for (ObjectInfo<? extends ActionFilter> objectInfo : filters) {
				filterNames.add(objectInfo.getType().getSimpleName());
			}
			expMethod.put("filters", filterNames);
		}
		Api api = ai.getMethod().getAnnotation(Api.class);
		if (api != null) {
			expMethod.put("author", api.author());
			expMethod.put("name", api.name());
			expMethod.put("description", api.description());
		} else {
			expMethod.put("name", expMethod.get("methodName"));
		}
		expMethod.put("params", make(ai.getMethod(), expMethod, ctx));
		return expMethod;
	}
	
	/**
	 * 根据索引匹配
	 * @param params
	 * @param index
	 * @param name 
	 * @return
	 */
	protected ApiParam matchApiParam(List<ApiParam> params,int index, String name) {
		if (params == null || params.size() == 0) {
			return null;
		}
		for (ApiParam apiParam : params) {
			if (apiParam.index() == index || Strings.equals(name, apiParam.name())) {
				return apiParam;
			}
		}
		return null;
	}

	protected List<ExpParam> make(Method method, ExpMethod expMethod, ExpContext ctx) {
		
		//TODO 这里的逻辑值得商榷
		
		/**
		 * 1.根据方法获取注解<br>
		 * 2.根据方法获取参数列表<br>
		 * 3.如果注解中存在params配置<br>
		 * 	3.1  遍历配置
		 * 	3.2 根据配置对象获取对应参数类型进行数据组装
		 * 4.如果没有配置
		 * 	4.1 根据参数列表进行组装
		 * 
		 */
		List<ExpParam> params = new ArrayList<>();
		String metaKey = ClassMetaReader.getKey(method);
		expMethod.put("methodId", ctx.expClass().getString("typeName") + "#" + metaKey);
		ClassMeta meta = ctx.expClass().getAs("meta", ClassMeta.class);
		List<String> paramNames = meta == null ? null : meta.paramNames.get(metaKey);// 参数名列表
		Api api = method.getAnnotation(Api.class);
		List<ApiParam> apiParams = null;
		if (api != null) {
			apiParams = Lang.array2list(api.params());
		}
		Type[] types = method.getGenericParameterTypes();
		Annotation[][] annos = method.getParameterAnnotations();
		for (int i = 0; i < types.length; i++) {
			Mirror<?> mirror = Mirror.me(types[i]);
			Class<?> clazz = mirror.getType();
			if (clazz.isAssignableFrom(HttpServletRequest.class) || clazz.isAssignableFrom(HttpServletResponse.class)
					|| clazz.isAssignableFrom(HttpSession.class) || clazz.isAssignableFrom(ServletContext.class)) {//Servlet相关的类型直接跳过
				continue;
			}
			ExpParam expParam = new ExpParam();
			expParam.put("index", i); 
			expParam.put("ignore", false); 
			expParam.put("paramLocalName", paramNames.get(i));
			expParam.put("typeName", mirror.getType().getName());
			expParam.put("requestData", instance(clazz));
			for (Annotation anno : annos[i]) {// 尝试获取Param注解
				if (anno instanceof Param) {
					Param _param = (Param) anno;
					expParam.put("annoParamName", _param.value());
					expParam.put("annoParamDefault", _param.df());
					expParam.put("paramDefault", _param.df());
					expParam.put("annoParamDateFormat", _param.dfmt());
					expParam.put("paramDateFormat", _param.dfmt());
				} 
			}
			// 把参数名先定下来
			if (!Strings.isBlank(expParam.getString("annoParamName")))
				expParam.put("paramName", expParam.getString("annoParamName"));
			else
				expParam.put("paramName", expParam.getString("paramLocalName"));
			
			//尝试获取apiParam对象
			ApiParam apiParam = matchApiParam(apiParams,i,expParam.getString("paramName"));
			if (apiParam!=null) {
				if (apiParam.index() != -1) {
					expParam.put("index", apiParam.index()); 
				}
				if (Strings.isBlank(apiParam.name())) {
					expParam.put("paramName", apiParam.name());
				}
				expParam.put("ignore", apiParam.ignore());
				expParam.put("description", apiParam.description());
				if (!Strings.isBlank(apiParam.type()))
					expParam.put("typeName", apiParam.type());
				if (!Strings.isBlank(apiParam.defaultValue()))
					expParam.put("paramDefault", apiParam.defaultValue());
				if (!Strings.isBlank(apiParam.dateFormat()))
					expParam.put("paramDateFormat", apiParam.dateFormat());
				expParam.put("optional", apiParam.optional());
				if (!Strings.isBlank(apiParam.requestData()))
					expParam.put("requestData", apiParam.requestData());
				apiParams.remove(apiParam);//处理完成 移除之
			}
			params.add(expParam);
		}
		//检查一下没有处理到的注解
		if (apiParams!=null) {
			for (ApiParam apiParam : apiParams) {
				ExpParam expParam = new ExpParam();
				expParam.put("requestData", apiParam.requestData());
				if (apiParam.index() != -1) {
					expParam.put("index", apiParam.index()); 
				}
				if (Strings.isBlank(apiParam.name())) {
					expParam.put("paramName", apiParam.name());
				}
				expParam.put("ignore", apiParam.ignore());
				expParam.put("paramName", apiParam.name());
				expParam.put("description", apiParam.description());
				if (!Strings.isBlank(apiParam.type()))
					expParam.put("typeName", apiParam.type());
				if (!Strings.isBlank(apiParam.defaultValue()))
					expParam.put("paramDefault", apiParam.defaultValue());
				if (!Strings.isBlank(apiParam.dateFormat()))
					expParam.put("paramDateFormat", apiParam.dateFormat());
				expParam.put("optional", apiParam.optional());
				params.add(expParam);
			}
		}
		// 处理一下返回和异常
		ReturnKey[] oks = null;
		ReturnKey[] fails = null;
		if (api != null) {
			oks = api.ok();
			fails = api.fail();
		}
		if (oks != null) {//有配置
			final List<NutMap> data = new ArrayList<NutMap>();
			Lang.each(oks, new Each<ReturnKey>() {

				@Override
				public void invoke(int index, ReturnKey key, int length) throws ExitLoop, ContinueLoop, LoopException {
					NutMap temp = NutMap.NEW();
					temp.put("key", key.key());
					temp.put("description", key.description());
					data.add(temp);
				}
			});
			expMethod.put("oks", data);
		}
		if (fails != null && fails.length!=0) {//有配置
			final List<NutMap> data = new ArrayList<NutMap>();
			Lang.each(fails, new Each<ReturnKey>() {

				@Override
				public void invoke(int index, ReturnKey key, int length) throws ExitLoop, ContinueLoop, LoopException {
					NutMap temp = NutMap.NEW();
					temp.put("key", key.key());
					temp.put("description", key.description());
					data.add(temp);
				}
			});
			expMethod.put("fails", data);
		} else {
			expMethod.put("fails", defaultFails);
		}
		return params;
	}

}
