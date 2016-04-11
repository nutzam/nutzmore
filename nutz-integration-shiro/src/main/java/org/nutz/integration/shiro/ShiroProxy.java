package org.nutz.integration.shiro;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.ExecutionException;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

public class ShiroProxy implements Subject {
    
    protected Subject proxy() {
        return SecurityUtils.getSubject();
    }

    public Object getPrincipal() {
        return proxy().getPrincipal();
    }

    public PrincipalCollection getPrincipals() {
        return proxy().getPrincipals();
    }

    public boolean isPermitted(String permission) {
        return proxy().isPermitted(permission);
    }

    public boolean isPermitted(Permission permission) {
        return proxy().isPermitted(permission);
    }

    public boolean[] isPermitted(String... permissions) {
        return proxy().isPermitted(permissions);
    }

    public boolean[] isPermitted(List<Permission> permissions) {
        return proxy().isPermitted(permissions);
    }

    public boolean isPermittedAll(String... permissions) {
        return proxy().isPermittedAll(permissions);
    }

    public boolean isPermittedAll(Collection<Permission> permissions) {
        return proxy().isPermittedAll(permissions);
    }

    public void checkPermission(String permission) throws AuthorizationException {
        proxy().checkPermission(permission);
    }

    public void checkPermission(Permission permission) throws AuthorizationException {
        proxy().checkPermission(permission);
    }

    public void checkPermissions(String... permissions) throws AuthorizationException {
        proxy().checkPermissions(permissions);
    }

    public void checkPermissions(Collection<Permission> permissions) throws AuthorizationException {
        proxy().checkPermissions(permissions);
    }

    public boolean hasRole(String roleIdentifier) {
        return proxy().hasRole(roleIdentifier);
    }

    public boolean[] hasRoles(List<String> roleIdentifiers) {
        return proxy().hasRoles(roleIdentifiers);
    }

    public boolean hasAllRoles(Collection<String> roleIdentifiers) {
        return proxy().hasAllRoles(roleIdentifiers);
    }

    public void checkRole(String roleIdentifier) throws AuthorizationException {
        proxy().checkRole(roleIdentifier);
    }

    public void checkRoles(Collection<String> roleIdentifiers) throws AuthorizationException {
        proxy().checkRoles(roleIdentifiers);
    }

    public void checkRoles(String... roleIdentifiers) throws AuthorizationException {
        proxy().checkRoles(roleIdentifiers);
    }

    public void login(AuthenticationToken token) throws AuthenticationException {
        proxy().login(token);
    }

    public boolean isAuthenticated() {
        return proxy().isAuthenticated();
    }

    public boolean isRemembered() {
        return proxy().isRemembered();
    }

    public Session getSession() {
        return proxy().getSession();
    }

    public Session getSession(boolean create) {
        return proxy().getSession(create);
    }

    public void logout() {
        proxy().logout();
    }

    public <V> V execute(Callable<V> callable) throws ExecutionException {
        return proxy().execute(callable);
    }

    public void execute(Runnable runnable) {
        proxy().execute(runnable);
    }

    public <V> Callable<V> associateWith(Callable<V> callable) {
        return proxy().associateWith(callable);
    }

    public Runnable associateWith(Runnable runnable) {
        return proxy().associateWith(runnable);
    }

    public void runAs(PrincipalCollection principals) throws NullPointerException, IllegalStateException {
        proxy().runAs(principals);
    }

    public boolean isRunAs() {
        return proxy().isRunAs();
    }

    public PrincipalCollection getPreviousPrincipals() {
        return proxy().getPreviousPrincipals();
    }

    public PrincipalCollection releaseRunAs() {
        return proxy().releaseRunAs();
    }
    
    //-------------------------------------------
    
    public boolean isGuest() {
        return isAuthenticated();
    }
    
    public boolean hasPermit(String permission) {
        return proxy().isPermitted(permission);
    }
}
