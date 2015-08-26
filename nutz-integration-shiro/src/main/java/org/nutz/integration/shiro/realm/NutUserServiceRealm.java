package org.nutz.integration.shiro.realm;

import java.util.List;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.nutz.ioc.Ioc;
import org.nutz.mvc.Mvcs;

public class NutUserServiceRealm extends AuthorizingRealm {
    
    protected String ctxName = "nutz";

    public NutUserServiceRealm() {
        super();
    }

    public NutUserServiceRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
        super(cacheManager, matcher);
    }

    public NutUserServiceRealm(CacheManager cacheManager) {
        super(cacheManager);
    }

    public NutUserServiceRealm(CredentialsMatcher matcher) {
        super(matcher);
    }

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        //null usernames are invalid
        if (principals == null) {
            throw new AuthorizationException("PrincipalCollection method argument cannot be null.");
        }
        String username = principals.getPrimaryPrincipal().toString();
        if (!us().hasUser(username))
            return null;
        if (us().isLocked(username)) 
            throw new LockedAccountException("Account [" + username + "] is locked.");
        if (us().isCredentialsExpired(username)) {
            String msg = "The credentials for account [" + username + "] are expired";
            throw new ExpiredCredentialsException(msg);
        }

        SimpleAuthorizationInfo auth = new SimpleAuthorizationInfo();
        List<String> roles = us().listRoles(username);
        if (roles != null)
            auth.addRoles(roles);
        List<String> permissions = us().listPermissions(username);
        if (permissions != null)
            auth.addStringPermissions(permissions);
        return auth;
    }

    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String username = upToken.getUsername();
        if (!us().hasUser(username))
            return null;
        if (us().isLocked(username)) 
            throw new LockedAccountException("Account [" + upToken.getUsername() + "] is locked.");
        if (us().isCredentialsExpired(username)) {
            String msg = "The credentials for account [" + username + "] are expired";
            throw new ExpiredCredentialsException(msg);
        }

        String[] ps = us().ps(username);
        if (ps == null)
            return null;
        SimpleAccount account = new SimpleAccount(username, ps[0], getName());
        if (ps.length > 1)
            account.setCredentialsSalt(ByteSource.Util.bytes(ps[1]));
        return account;
    }

    protected NutShiroUserService us() {
        Ioc ioc = Mvcs.ctx().iocs.get(ctxName);
        if (ioc == null)
            ioc = Mvcs.ctx().getDefaultIoc();
        return ioc.get(NutShiroUserService.class);
    }
    
    public void setCtxName(String ctxName) {
        this.ctxName = ctxName;
    }
}
