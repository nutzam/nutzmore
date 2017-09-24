package org.nutz.integration.jedisque;

import java.net.URI;
import java.net.URISyntaxException;

import org.nutz.ioc.impl.PropertiesProxy;

import com.github.xetorthio.jedisque.Jedisque;

/**
 * Created by Jianghao on 2017-09-24
 *
 * @howechiang
 */
public class JedisqueAgent {

    protected PropertiesProxy conf;

    public JedisqueAgent() {
    }

    public JedisqueAgent(PropertiesProxy conf) {
        this.conf = conf;
    }

    public void setConf(PropertiesProxy conf) {
        this.conf = conf;
    }

    public Jedisque build() throws URISyntaxException {
        String[] uris = conf.get("disque.uris").split(",");
        URI[] _uri = new URI[uris.length];
        for (int i = 0; i < _uri.length; i++) {
            _uri[i] = new URI(uris[i]);
        }
        return new Jedisque(_uri);
    }
}
