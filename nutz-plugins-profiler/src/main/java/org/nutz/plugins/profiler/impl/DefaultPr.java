package org.nutz.plugins.profiler.impl;

import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.profiler.Pr;
import org.nutz.plugins.profiler.PrSpan;

public class DefaultPr extends Pr {
    
    private static final Log log = Logs.get();
    
    protected static ThreadLocal<PrSpan> ps = new ThreadLocal<PrSpan>();

    public PrSpan begin(String type, String trace_id, String parent_id, String name, NutMap metas) {
        PrSpan span = new PrSpan();
        span.setCreateTime(System.currentTimeMillis());
        span.setSpanId(R.UU32());
        PrSpan pspan = ps.get();
        if (trace_id == null) {
            if (pspan == null) {
                trace_id = R.UU32();
            } else {
                trace_id = pspan.getTraceId();
            }
        }
        span.setMetas(metas);
        span.setTraceId(trace_id);
        span.setSpantype(type);
        span.setHook(this);
        if (pspan == null)
            ps.set(span);
        return span;
    }
    
    public void invoke(final PrSpan span) {
        if (ps.get() == span)
            ps.remove();
        if (span == null || span.getSpanId() == null)
            return;
        span.setSpantime(System.currentTimeMillis() - span.getCreateTime());
        if (storage != null || es != null) {
            es.submit(new Runnable() {
                public void run() {
                    try {
                        storage.save(span);
                    } catch (Throwable e) {
                        if (log.isTraceEnabled())
                            log.trace("record profile span fail", e);
                    }
                }
            });
        }
    }
}
