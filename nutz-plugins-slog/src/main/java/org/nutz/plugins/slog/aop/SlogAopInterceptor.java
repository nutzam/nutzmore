package org.nutz.plugins.slog.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.nutz.aop.InterceptorChain;
import org.nutz.aop.MethodInterceptor;
import org.nutz.el.El;
import org.nutz.ioc.Ioc;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.slog.annotation.Slog;
import org.nutz.plugins.slog.service.SlogService;

public class SlogAopInterceptor implements MethodInterceptor {

    private static final Log log = Logs.get();

    protected SlogService slogService;

    protected String source;

    protected String tag;
    protected CharSegment before;
    protected CharSegment after;
    protected CharSegment error;
    protected boolean async;
    protected Map<String, El> els;
    protected Ioc ioc;

    public SlogAopInterceptor(Ioc ioc, Slog slog, Method method) {
        els = new HashMap<String, El>();
        if (!Strings.isBlank(slog.before())) {
            before = new CharSegment(slog.before());
            for (String key : before.keys()) {
                els.put(key, new El(key));
            }
        }
        if (!Strings.isBlank(slog.after())) {
            after = new CharSegment(slog.after());
            for (String key : after.keys()) {
                els.put(key, new El(key));
            }
        }
        if (!Strings.isBlank(slog.error())) {
            error = new CharSegment(slog.error());
            for (String key : error.keys()) {
                els.put(key, new El(key));
            }
        }
        this.ioc = ioc;
        this.source = method.getDeclaringClass().getName() + "#" + method.getName();
        this.tag = slog.tag();
        Slog _s = method.getDeclaringClass().getAnnotation(Slog.class);
        if (_s != null) {
            this.tag = _s.tag() + "," + this.tag;
        }
        this.async = slog.async();
    }

    public void filter(InterceptorChain chain) throws Throwable {
        if (before != null)
            doLog("aop.before", before, chain, null);
        try {
            chain.doChain();
            if (after != null)
                doLog("aop.after", after, chain, null);
        }
        catch (Throwable e) {
            if (error != null)
                doLog("aop.error", error, chain, e);
            throw e;
        }
    }

    protected void doLog(String t, CharSegment seg, InterceptorChain chain, Throwable e) {
        if (slogService == null)
            slogService = ioc.get(SlogService.class);
        try {
            slogService.log(t,
                            tag,
                            source,
                            seg,
                            els,
                            async,
                            chain.getArgs(),
                            chain.getReturn(),
                            chain.getCallingMethod(),
                            chain.getCallingObj(),
                            e);
        }
        catch (Exception e1) {
            log.debug("slog fail", e1);
        }
    }
}
