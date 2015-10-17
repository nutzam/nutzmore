package org.nutz.plugins.oauth2.server.entity;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("oauth2_user")
public class OAuthUser implements Serializable {

    private static final long serialVersionUID = -8601682198825685373L;

    @Id
    @Comment("编号")
    private Long id;
    @Column
    @Comment("用户名")
    private String username;
    @Column
    @Comment("密码")
    private String password;
    @Column
    @Comment("加密密码的盐")
    private String salt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCredentialsSalt() {
        return username + salt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        OAuthUser user = (OAuthUser) o;

        if (id != null ? !id.equals(user.id) : user.id != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{"
               + "id="
               + id
               + ", username='"
               + username
               + '\''
               + ", password='"
               + password
               + '\''
               + ", salt='"
               + salt
               + '\''
               + '}';
    }
}
