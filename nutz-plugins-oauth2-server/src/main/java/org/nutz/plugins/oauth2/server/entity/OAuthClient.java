package org.nutz.plugins.oauth2.server.entity;

import java.io.Serializable;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("oauth2_client")
public class OAuthClient implements Serializable {

    private static final long serialVersionUID = 2345879369118031587L;
    @Id
    private Long id;
    
    @Column("client_name")
    private String clientName;
    
    @Column("client_id")
    private String clientId;
    
    @Column("client_secret")
    private String clientSecret;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        OAuthClient client = (OAuthClient) o;

        if (id != null ? !id.equals(client.id) : client.id != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Client{"
               + "id="
               + id
               + ", clientName='"
               + clientName
               + '\''
               + ", clientId='"
               + clientId
               + '\''
               + ", clientSecret='"
               + clientSecret
               + '\''
               + '}';
    }
}
