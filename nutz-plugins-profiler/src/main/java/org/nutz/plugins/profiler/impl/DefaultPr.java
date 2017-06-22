package org.nutz.plugins.profiler.impl;

import org.nutz.lang.Strings;
import org.nutz.lang.random.R;
import org.nutz.lang.util.Node;
import org.nutz.lang.util.Nodes;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.plugins.profiler.Pr;
import org.nutz.plugins.profiler.PrContext;
import org.nutz.plugins.profiler.PrSpan;

public class DefaultPr extends Pr {
    
    private static final Log log = Logs.get();
    
    protected static ThreadLocal<PrContext> _ctx = new ThreadLocal<PrContext>();

    public PrSpan begin(String name, String trace_id, String parent_id) {
        PrSpan span = new PrSpan();
        span.setTimestamp(System.currentTimeMillis()*1000);
        span.setId(R.UU32().substring(0, 16));
        PrContext ctx = _ctx.get();
        Node<PrSpan> node = Nodes.create(span);
        if (ctx == null) { // 我是顶层span
            ctx = new PrContext();
            ctx.current = node;
            _ctx.set(ctx);
            if (Strings.isBlank(trace_id))
                trace_id = R.UU16().substring(0, 16);
            span.setTraceId(trace_id);
        } else {
            // 上层已经有span了,继承它
            PrSpan parent = ctx.current.get();
            ctx.current.add(node);
            ctx.current = node;
            
            span.setTraceId(parent.getTraceId());
            span.setParentId(parent.getId());
        }
        span.setName(name);
        span.setHook(this);
        // 最后才设置开始时间
        span.setBegin(System.nanoTime()/1000);
        return span;
    }
    
    public void invoke(final PrSpan span) {
        PrContext ctx = _ctx.get();
        if (ctx == null)
            return;
        ctx.current = ctx.current.parent();
        if (ctx.current == null)
            _ctx.remove();
        span.setDuration(System.nanoTime()/1000 - span.getBegin());
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
