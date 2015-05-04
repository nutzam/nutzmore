package org.nutz.integration.shiro.realm.bean;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Table;

@Table("users")
public class User extends AbstractShiroBean {
    
    public User() {}

	public User(String name, String passwd, String slat) {
        setName(name);
        this.passwd = passwd;
        this.salt = slat;
    }
	
	@Column
	private transient String passwd;
	
	@Column
	private transient String salt;
	
	@ManyMany(from="uid", relation="user_role", target=Role.class, to="role_id")
	private List<Role> roles;
	
	@ManyMany(from="uid", relation="user_permission", target=Permission.class, to="permission_id")
	private List<Permission> permissions;

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
	
	public Set<String> getRolePermissionStrSet() {
	    Set<String> permissionStrs = new HashSet<String>();
	    if (roles != null) {
	        for (Role role : roles) {
                if (role.getPermissions() == null)
                    continue;
                for (Permission permission : role.getPermissions()) {
                    permissionStrs.add(permission.getName());
                }
            }
	    }
	    return permissionStrs;
	}
}
