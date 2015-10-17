package org.nutz.plugins.oauth2.server.service;

import java.util.List;

import org.nutz.plugins.oauth2.server.entity.OAuthClient;

public interface OAuthClientService {

    public OAuthClient createClient(OAuthClient client);

    public OAuthClient updateClient(OAuthClient client);

    public void deleteClient(Long clientId);

    OAuthClient findOne(Long clientId);

    List<OAuthClient> findAll();

    OAuthClient findByClientId(String clientId);

    OAuthClient findByClientSecret(String clientSecret);
}
