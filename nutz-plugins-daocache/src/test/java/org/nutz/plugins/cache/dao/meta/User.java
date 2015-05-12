package org.nutz.plugins.cache.dao.meta;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.Table;

@Table("tb_user")
public class User implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    private long id;
    @Name
    private String name;
    
    private String email;
    
    @One(target=UserProfile.class, field="id", key="userId")
    private UserProfile profile;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }

}
