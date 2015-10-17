package org.nutz.plugins.oauth2.server.service.impl;

import java.util.List;

import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.random.R;
import org.nutz.plugins.oauth2.server.entity.OAuthClient;
import org.nutz.plugins.oauth2.server.service.OAuthClientService;
import org.nutz.service.EntityService;

@IocBean(name = "oAuthClientService", fields = { "dao" })
public class OauthClientServiceImpl extends EntityService<OAuthClient> implements OAuthClientService {

	@Override
	public OAuthClient createClient(OAuthClient client) {
		client.setClientId(R.UU32());
		client.setClientSecret(R.UU32());
		return dao().insert(client);
	}

	@Override
	public OAuthClient updateClient(OAuthClient client) {
		dao().update(client);
		return client;
	}

	@Override
	public void deleteClient(Long clientId) {
		dao().delete(getEntityClass(), clientId);
	}

	@Override
	public OAuthClient findOne(Long clientId) {
		return dao().fetch(getEntityClass(), clientId);
	}

	@Override
	public List<OAuthClient> findAll() {
		return dao().query(getEntityClass(), null);
	}

	@Override
	public OAuthClient findByClientId(String clientId) {
		return dao().fetch(getEntityClass(), Cnd.where("clientId", "=", clientId));
	}

	@Override
	public OAuthClient findByClientSecret(String clientSecret) {
		return dao().fetch(getEntityClass(), Cnd.where("clientSecret", "=", clientSecret));
	}
}
