package org.nutz.plugins.profiler;

import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.util.Callback;
import org.nutz.lang.util.NutMap;

/**
 * 描述一个时间片段
 * 
 * @author wendal
 *
 */
@Table("t_pr_span")
public class PrSpan {
    // 跟踪的主id
    protected String traceId;
    // 父跟踪id,备用
    protected String parentId;
    // 时间片id
    protected String spanId;
    // 时间片的名字
    protected String name;
    // 时间片的类型,例如sql,request,mongodb,jedis
    protected String spantype;
    // 附加的元数据,备用
    protected NutMap metas;
    // 时间片创建的时间
    protected long createTime;
    // 时间片所耗费的时间
    protected long spantime;
    // 用于关闭并存在本时间片的钩子
    protected transient Callback<PrSpan> hook;

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NutMap getMetas() {
        return metas;
    }

    public void setMetas(NutMap metas) {
        this.metas = metas;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getSpantime() {
        return spantime;
    }

    public void setSpantime(long spantime) {
        this.spantime = spantime;
    }

    public String getSpantype() {
        return spantype;
    }

    public void setSpantype(String spantype) {
        this.spantype = spantype;
    }

    // -------------------------------------------
    public void end() {
        Callback<PrSpan> hook = this.hook;
        if (hook != null) {
            this.hook = null;
            hook.invoke(this);
        }
    }

    public void setHook(Callback<PrSpan> hook) {
        this.hook = hook;
    }
}
