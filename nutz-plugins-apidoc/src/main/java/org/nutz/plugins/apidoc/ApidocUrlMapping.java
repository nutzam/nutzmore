package org.nutz.plugins.apidoc;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
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
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.impl.ActionInvoker;
import org.nutz.mvc.impl.UrlMappingImpl;
import org.nutz.mvc.view.RawView;
import org.nutz.mvc.view.UTF8JsonView;
import org.nutz.plugins.apidoc.annotation.Api;
import org.nutz.plugins.apidoc.annotation.ApiMatchMode;

/**
 * Api文档生成
 * @author wendal
 *
 */
public class ApidocUrlMapping extends UrlMappingImpl {

    /**
     * 按类(或组?)分类排好的列表
     */
    protected LinkedHashMap<String, ExpClass> infos = new LinkedHashMap<>();

    private static final Log log = Logs.get();

    protected static String[] EMTRY = new String[0];

    public void add(ActionChainMaker maker, ActionInfo ai, NutConfig nc) {
        super.add(maker, ai, nc);
        ExpContext ctx = new ExpContext();
        ctx.set("maker", maker);
        ctx.set("ai", ai);
        ctx.set("nc", nc);
        _add(ctx);
    }

    public ActionInvoker get(ActionContext ac) {
        // 如果是读取expPath,俺就自行处理了
        String path = Mvcs.getRequestPath(ac.getRequest());
        if (path.startsWith(expPath)) {
            if (path.equals(expPath) || path.equals(expPath + "index")) {
                HttpServletResponse resp = ac.getResponse();
                resp.setCharacterEncoding("UTF-8");
                resp.setContentType("text/html");
                try {
                    new RawView("html").render(ac.getRequest(), resp, getClass().getResourceAsStream("index.html"));
                }
                catch (Throwable e) {
                    log.debug(e.getMessage(), e);
                }
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
        public boolean invoke(ActionContext ac) {
            return true;
        }
    }

    class DocActionInvoker extends ActionInvoker {
        public boolean invoke(ActionContext ac) {
            try {
                ac.getResponse().setCharacterEncoding("UTF-8");
                NutMap re = new NutMap("data", infos);
                re.put("content_path", ac.getRequest().getContextPath());
                view.render(ac.getRequest(), ac.getResponse(), re);
            }
            catch (Throwable e) {
                log.debug("exp fail", e);
            }
            return true;
        }
    }

    protected static class ExpClass extends NutMap {
        private static final long serialVersionUID = 1L;
//        String name;
//        String typeName;
//        String description;
//        String iocName;
//        String[] pathPrefixs;
//        List<ExpMethod> methods = new ArrayList<>();
        // TODO 是不是应该加上作者
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
//        String paramName;
//        String paramVale;
//        String defaultValue;
//        String defaultDateFormat;
//        String className;
//        String attrName;
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
        if (api.match() == ApiMatchMode.NONE)
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
        expClass.put("pathPrefixs",  at == null ? new String[0] : at.value());
        InputStream ins = klass.getClassLoader().getResourceAsStream(klass.getName().replace(".", "/") + ".class");
        if (ins != null) {
            try {
                ClassMeta meta = ClassMetaReader.build(ins);
                expClass.setv("meta", meta);
            }
            catch (Exception e) {
            }
        }
        return expClass;
    }
    
    protected ExpMethod makeMethod(ExpContext ctx) {
        ActionInfo ai = ctx.ai();
        ExpMethod expMethod = new ExpMethod();
        ctx.set("expMethod", expMethod);
        expMethod.put("chainName", ai.getChainName() == null ? "default" : ai.getChainName());
        expMethod.put("typeName", ai.getModuleType().getName());
        expMethod.put("okView", ai.getOkView());
        expMethod.put("failView", ai.getFailView());
        expMethod.put("httpMethods", ai.getHttpMethods());
        expMethod.put("lineNumber", ai.getLineNumber());
        expMethod.put("paths", ai.getPaths());

        expMethod.put("methodName", ai.getMethod().getName());
        if (ai.getAdaptorInfo() != null)
            expMethod.put("adaptorName", ai.getAdaptorInfo().getType().getSimpleName());
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
        
        expMethod.put("params", make(ai.getMethod(), expMethod, ctx));
        return expMethod;
    }
    
    protected List<ExpParam> make(Method method, ExpMethod expMethod, ExpContext ctx) {
        String metaKey = ClassMetaReader.getKey(method);
        expMethod.put("methodId", ctx.expClass().getString("typeName") + "#" + metaKey);
        ClassMeta meta = ctx.expClass().getAs("meta", ClassMeta.class);
        List<String> paramNames = meta == null ? null : meta.paramNames.get(metaKey);
        // TODO 还得解析参数
        List<ExpParam> params = new ArrayList<>();
        Annotation[][] annos = method.getParameterAnnotations();
        Type[] types = method.getGenericParameterTypes();
        for (int i = 0; i < types.length; i++) {
            ExpParam expParam = new ExpParam();
            expParam.put("index", i); // 实际顺序
            if (paramNames != null)
                expParam.put("paramLocalName", paramNames.get(i));
            else
                expParam.put("paramLocalName", "arg"+i);
            Mirror<?> mirror = Mirror.me(types[i]);
            expParam.put("typeName", mirror.getType().getName());
            
            // TODO 按不同注解可以分别处理
            for (Annotation anno : annos[i]) {
                if (anno instanceof Param) {
                    Param _param = (Param)anno;
                    expParam.put("annoParamName", _param.value());
                    expParam.put("annoParamDefault", _param.df());
                    expParam.put("annoParamDateFormat", _param.dfmt());
                } else if (anno instanceof Attr) {
                    Attr attr = (Attr)anno;
                    expParam.put("annoAttrName", attr.value());
                    expParam.put("annoAttrScope", attr.scope());
                }
            }
            if (Strings.isBlank(expParam.getString("paramName"))) {
                if (!Strings.isBlank(expParam.getString("annoParamName")))
                    expParam.put("paramName", expParam.getString("annoParamName"));
                else
                    expParam.put("paramName", expParam.getString("paramLocalName"));
            }
            
            params.add(expParam);
        }
        //Api api = method.getAnnotation(Api.class);
        return params;
    }
    
}















