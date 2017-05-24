package org.nutz.plugins.profiler.impl;

import javax.servlet.http.HttpServletRequest;

import org.nutz.lang.random.R;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.plugins.profiler.Pr;
import org.nutz.plugins.profiler.PrSpan;

public class PrProcessor extends AbstractProcessor {

    protected static final String ProfilerTraceIdHeader = "Nutz-Pr-Id";

    public void process(ActionContext ac) throws Throwable {
        HttpServletRequest req = ac.getRequest();
        String trace_id = req.getHeader(ProfilerTraceIdHeader);
        if (trace_id == null) {
            trace_id = R.UU32();
            ac.getResponse().setHeader(ProfilerTraceIdHeader, trace_id);
        }
        PrSpan span = Pr.begin("req", ac.getPath(), trace_id, null);
        try {
            doNext(ac);
        }
        finally {
            span.end();
        }
    }

}
