package org.nutz.integration.shiro;

import org.apache.shiro.authc.HostAuthenticationToken;
import org.apache.shiro.authc.RememberMeAuthenticationToken;

/**
 * 不携带校验信息,特供入口方法调用login操作
 * @author wendal
 *
 */
public class SimpleShiroToken implements HostAuthenticationToken, RememberMeAuthenticationToken{

	private static final long serialVersionUID = -1L;

	protected Object principal;
	
	protected boolean rememberMe;
	
	protected String host;
	
	public Object getPrincipal() {
		return principal;
	}

	public Object getCredentials() {
		return null;
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}
	
	public SimpleShiroToken() {
	}

	public SimpleShiroToken(Object principal) {
		this.principal = principal;
	}

	public SimpleShiroToken(Object principal, boolean rememberMe, String host) {
		this.principal = principal;
		this.rememberMe = rememberMe;
		this.host = host;
	}
}
