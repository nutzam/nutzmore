package org.nutz.plugins.slog.bean;

import java.io.Serializable;
import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_syslog_${ym}")
public class SlogBean implements Serializable {

	private static final long serialVersionUID = 4048681972879639280L;

	@Id
	private long id;
	
	@Column("t")// aop.before aop.after aop.error
	private String t;
	
	@Column("tg")
	private String tag; 
	
	@Column("src")
	@ColDefine(width=1024)
	private String source;
	
	@Column("u_id")
	private int uid;
	
	@Column("ip")
	private String ip;
	
	@Column
	@ColDefine(width=8192)
	private String msg;

	@Column("ct")
	protected Date createTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
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

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
