package org.nutz.plugins.daomapper.bean;

import java.io.Serializable;
import java.util.List;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;


@Table("t_user")
public class User implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	protected int id;
	@Name
	@Column
	protected String name;
	@Column("passwd")
	@ColDefine(width=128)
	protected String password;
	@Column
	protected String salt;
	@Column
	protected boolean locked;
    @ManyMany(from="u_id", relation="t_user_role", target=Role.class, to="role_id")
    protected List<Role> roles;
    @ManyMany(from="u_id", relation="t_user_permission", target=Permission.class, to="permission_id")
    protected List<Permission> permissions;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	
	public boolean isLocked() {
		return locked;
	}
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
    public List<Role> getRoles() {
        return roles;
    }
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    public List<Permission> getPermissions() {
        return permissions;
    }
    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
