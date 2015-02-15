package org.nutz.integration.shiro.realm.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;

@Table("t_user")
public class User {
    
    public User() {}

	public User(String name, String passwd, String slat) {
        this.name = name;
        this.passwd = passwd;
        this.salt = slat;
    }

    @Id
	private long id;
	
	@Name
	@Column
	private String name;
	
	@Column
	private transient String passwd;
	
	@Column
	private transient String salt;
	
	private boolean locked;
	
	@ManyMany(from="uid", relation="user_role", target=Role.class, to="role_id")
	private List<Role> roles;
	
	@ManyMany(from="uid", relation="user_permission", target=Permission.class, to="permission_id")
	private List<Permission> permissions;

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

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
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

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public String getSalt() {
        return salt;
    }
	
	public void setSalt(String salt) {
        this.salt = salt;
    }
	
	

	//------------------------------------------
	public Set<String> getRoleStrSet() {
		Set<String> roleStrs = new HashSet<String>();
		if (roles != null) {
			for (Role role : roles) {
				roleStrs.add(role.getName());
			}
		}
		return roleStrs;
	}
	
	public Set<String> getPermissionStrSet() {
		Set<String> permissionStrs = new HashSet<String>();
		if (permissions != null) {
			for (Permission permission : permissions) {
				permissionStrs.add(permission.getName());
			}
		}
		return permissionStrs;
	}
}
