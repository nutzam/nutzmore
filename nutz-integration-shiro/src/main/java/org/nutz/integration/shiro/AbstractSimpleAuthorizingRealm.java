package org.nutz.integration.shiro;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.nutz.dao.Dao;
import org.nutz.mvc.Mvcs;

public abstract class AbstractSimpleAuthorizingRealm extends AuthorizingRealm {
    
    protected Dao _dao;

    protected abstract AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals);

    protected abstract AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException;
    
    /**
     * 覆盖父类的验证,直接pass
     */
    protected void assertCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) throws AuthenticationException {
    }

    public AbstractSimpleAuthorizingRealm() {
        this(null, null);
    }

    public AbstractSimpleAuthorizingRealm(CacheManager cacheManager, CredentialsMatcher matcher) {
        super(cacheManager, matcher);
        // 设置token类型是关键!!!
        setAuthenticationTokenClass(SimpleShiroToken.class);
    }

    public AbstractSimpleAuthorizingRealm(CacheManager cacheManager) {
        this(cacheManager, null);
    }

    public AbstractSimpleAuthorizingRealm(CredentialsMatcher matcher) {
        this(null, matcher);
    }

    public Dao dao() {
        if (_dao == null) {
            _dao = Mvcs.ctx().getDefaultIoc().get(Dao.class, "dao");
            return _dao;
        }
        return _dao;
    }

    public void setDao(Dao dao) {
        this._dao = dao;
    }

}
