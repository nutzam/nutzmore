package org.nutz.plugins.cache.dao.meta;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Table;

@Table("tb_user_profile")
public class UserProfile implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Column("u_id")
    private long userId;

    @Column("loc")
    private String location;
    
    @Column("sex")
    private String sex;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
