package org.nutz.integration.shiro.realm;

import java.util.List;

public interface NutShiroUserService {

    boolean hasUser(String name);
    
    boolean isLocked(String name);
    
    boolean isCredentialsExpired(String name);
    
    List<String> listRoles(String name);
    
    List<String> listPermissions(String name);
    
    String[] ps(String name);
}
