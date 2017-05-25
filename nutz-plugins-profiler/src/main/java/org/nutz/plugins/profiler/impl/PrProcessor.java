package org.nutz.plugins.profiler.impl;

import javax.servlet.http.HttpServletRequest;

import org.nutz.mvc.ActionContext;
import org.nutz.mvc.impl.processor.AbstractProcessor;
import org.nutz.plugins.profiler.Pr;
import org.nutz.plugins.profiler.PrSpan;

public class PrProcessor extends AbstractProcessor {

    protected static final String TraceIdHeader = "X-B3-TraceId";
    protected static final String SpanIdHeader = "X-B3-SpanId";
    protected static final String ParentSpanIdHeader = "X-B3-ParentSpanId";
    protected static final String SimpledHeader = "X-B3-Sampled";
    protected static final String FlagsHeader = "X-B3-Flags";

    public void process(ActionContext ac) throws Throwable {
        HttpServletRequest req = ac.getRequest();
        String trace_id = req.getHeader(TraceIdHeader);
        String parent_id = req.getHeader(ParentSpanIdHeader);
        //String simpled = req.getHeader(SimpledHeader);
        //String flags = req.getHeader(FlagsHeader);
        PrSpan span = Pr.me().begin("http."+req.getMethod(), trace_id, parent_id);
        try {
            ac.getResponse().setHeader(SpanIdHeader, span.getId());
            doNext(ac);
        }
        finally {
            span.end();
        }
    }

}
