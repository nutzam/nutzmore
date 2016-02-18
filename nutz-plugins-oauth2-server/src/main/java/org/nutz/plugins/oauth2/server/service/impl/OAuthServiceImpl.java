package org.nutz.plugins.oauth2.server.service.impl;

import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.plugins.oauth2.server.service.OAuthClientService;
import org.nutz.plugins.oauth2.server.service.OAuthService;
import org.nutz.repo.cache.simple.LRUCache;

@IocBean(name = "oAuthService")
public class OAuthServiceImpl implements OAuthService {

	private LRUCache<String, String> cache = new LRUCache<String, String>(1024);

	@Inject
	private OAuthClientService oAuthClientService;

	@Override
	public void addAuthCode(String authCode, String username) {
		cache.put(authCode, username);
	}

	@Override
	public void addAccessToken(String accessToken, String username) {
		cache.put(accessToken, username);
	}

	@Override
	public String getUsernameByAuthCode(String authCode) {
		return cache.get(authCode);
	}

	@Override
	public String getUsernameByAccessToken(String accessToken) {
		return cache.get(accessToken);
	}

	@Override
	public boolean checkAuthCode(String authCode) {
		return cache.get(authCode) != null;
	}

	@Override
	public boolean checkAccessToken(String accessToken) {
		return cache.get(accessToken) != null;
	}

	@Override
	public boolean checkClientId(String clientId) {
		return oAuthClientService.findByClientId(clientId) != null;
	}

	@Override
	public boolean checkClientSecret(String clientSecret) {
		return oAuthClientService.findByClientSecret(clientSecret) != null;
	}

	@Override
	public long getExpireIn() {
		return 3600L;
	}
}
