package org.nutz.plugins.slog.bean;

import java.io.Serializable;
import java.util.Date;

import org.nutz.dao.entity.annotation.*;

@Table("t_syslog_${ym}")
public class SlogBean implements Serializable {

    private static final long serialVersionUID = 4048681972879639280L;
    
    @Column
    @Prev(els={@EL("uuid()")})
    protected String uu32;

    @Column("t") // aop.before aop.after aop.error
    protected String t;

    @Column("tg")
    protected String tag;

    @Column("src")
    @ColDefine(width = 1024)
    protected String source;

    @Column("u_id")
    protected long uid;

    @Column("u_name")
    protected String username;

    @Column("ip")
    protected String ip;

    @Column
    @ColDefine(width = 4000)
    protected String msg;

    @Column("ct")
    protected Date createTime;

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
