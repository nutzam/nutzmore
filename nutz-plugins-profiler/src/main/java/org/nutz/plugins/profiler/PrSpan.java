package org.nutz.plugins.profiler;

import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
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
    @Column("trace_id")
    protected String traceId;
    // 父跟踪id,备用
    @Column("parent_id")
    protected String parentId;
    // 时间片id
    @Column("span_id")
    protected String id;
    // 时间片的名字
    @Column("nm")
    protected String name;
    // 附加的元数据,备用
    @Column
    @ColDefine(width=8096)
    protected List<NutMap> annotations;
    // 附加的元数据,备用
    @Column
    @ColDefine(width=8096)
    protected List<NutMap> binaryAnnotations;
    // 时间片创建的时间
    @Column("tt")
    protected long timestamp;
    // 时间片所耗费的时间
    @Column("du")
    protected long duration;
    // 存储时间片开始的纳秒数
    protected transient long begin;
    // 用于关闭并存在本时间片的钩子
    protected transient Callback<PrSpan> hook;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<NutMap> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<NutMap> annotations) {
        this.annotations = annotations;
    }

    public List<NutMap> getBinaryAnnotations() {
        return binaryAnnotations;
    }

    public void setBinaryAnnotations(List<NutMap> binaryAnnotations) {
        this.binaryAnnotations = binaryAnnotations;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getBegin() {
        return begin;
    }

    public void setBegin(long begin) {
        this.begin = begin;
    }
    
}
