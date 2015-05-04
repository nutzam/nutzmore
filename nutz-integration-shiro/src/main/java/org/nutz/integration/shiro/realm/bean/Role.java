package org.nutz.integration.shiro.realm.bean;

import java.util.List;

import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Table;

@Table("roles")
public class Role extends AbstractShiroBean {
	

	@ManyMany(from="role_id", relation="role_permission", target=Permission.class, to="permission_id")
	private List<Permission> permissions;

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
	
}
