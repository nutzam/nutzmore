package org.nutz.plugins.jqgrid.entity;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("test_user")
public class TestUser {
	@Id
	@ColDefine(type = ColType.INT, width = 11,customType="int")
	private Integer id;
	
	@ColDefine(type = ColType.VARCHAR, width = 255,notNull=true)
	@Comment("用户名")
	private String username;
	
	@ColDefine(type = ColType.VARCHAR, width = 255,notNull=true)
	@Comment("密码")
	private String password;
	
	@ColDefine(type = ColType.DATETIME,notNull=true)
	@Comment("注册时间")
	private Date createTime;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}
