package org.nutz.plugins.slog.bean;

import java.io.Serializable;
import java.util.Date;

import org.nutz.dao.entity.annotation.*;

/**
 * 日志实体
 */
@Table("t_syslog_${ym}")
public class SlogBean implements Serializable {

    private static final long serialVersionUID = 4048681972879639280L;

    @Name
    @Column
    @Prev(els={@EL("uuid()")})
    protected String uu32;

    /**
     * aop.before aop.after aop.error
     */
    @Column("t")
    protected String t;

    @Column("tg")
    @Comment("系统模块")
    protected String tag;

    @Column("url")
    @Comment("请求地址")
    @ColDefine(width = 1024)
    protected String url;

    @Column("src")
    @Comment("操作方法")
    @ColDefine(width = 1024)
    protected String source;

    @Column
    @ColDefine(width = 4000)
    @Comment("操作信息")
    protected String msg;

    @Column("u_id")
    @Comment("操作用户")
    protected String uid;

    @Column("u_name")
    protected String username;

    @Column("param")
    @Comment("请求参数")
    @ColDefine(width = 255)
    protected String param;

    @Column("os")
    @Comment("操作系统")
    protected String os;

    @Column("browser")
    @Comment("客户端浏览器")
    protected String browser;

    @Column("ip")
    @Comment("主机地址")
    protected String ip;

    @Column("location")
    @Comment("操作地点")
    protected String location;

    @Column("ct")
    @Comment("操作时间")
    protected Date createTime;

    public String getUu32() {
        return uu32;
    }

    public void setUu32(String uu32) {
        this.uu32 = uu32;
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
