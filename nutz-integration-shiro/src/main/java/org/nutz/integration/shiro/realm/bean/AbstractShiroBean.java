package org.nutz.integration.shiro.realm.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;

public abstract class AbstractShiroBean {
    
    @Id
    private long id;

    @Name
    @Column("nm")
    private String name;
    
    @Column("dt")
    private String description;
    
    @Column("lkd")
    private boolean locked;
    
    @Column("ct")
    private Date createTime;
    
    @Column("ut")
    private Date updateTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    public boolean isLocked() {
        return this.locked;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
