package org.nutz.integration.shiro.matcher;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.nutz.lang.Lang;

/**
 * 
 * @author Kerbores(kerbores@gmail.com)
 *
 * @project nutz-integration-shiro
 *
 * @file MD5PasswordMatcher.java
 *
 * @description MD5密码匹配器
 *
 * @time 2016年3月24日 上午11:59:18
 *
 */
public class MD5PasswordMatcher extends SimpleCredentialsMatcher {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.shiro.authc.credential.SimpleCredentialsMatcher#
	 * doCredentialsMatch (org.apache.shiro.authc.AuthenticationToken,
	 * org.apache.shiro.authc.AuthenticationInfo)
	 */
	@Override
	public boolean doCredentialsMatch(AuthenticationToken authcToken, AuthenticationInfo info) {
		UsernamePasswordToken token = (UsernamePasswordToken) authcToken;
		Object tokenCredentials = Lang.md5(new String(token.getPassword()));// MD5
		Object accountCredentials = getCredentials(info);
		return equals(tokenCredentials, accountCredentials);
	}
}
