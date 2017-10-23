package org.nutz.integration.neo4j;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.lang.Lang;

public class Neo4jFactory {

    public static Driver build(PropertiesProxy conf, String prefix) {
        boolean useSSL = conf.getBoolean(prefix + "useSSL", false);
        Config config;
        if (useSSL) {
            throw Lang.noImplement();
        } else {
            config = Config.build().withoutEncryption().toConfig();
        }
        String driverURL = conf.get(prefix + "url", "bolt://localhost:7687");
        String username = conf.get(prefix + "user", "neo4j");
        String password = conf.get(prefix + "password", "123456");
        Driver driver = GraphDatabase.driver(driverURL,
                                             AuthTokens.basic(username, password),
                                             config); // <password>
        return driver;
    }
}
