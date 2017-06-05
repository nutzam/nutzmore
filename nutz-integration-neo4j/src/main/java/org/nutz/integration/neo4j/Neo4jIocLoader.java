package org.nutz.integration.neo4j;

import org.nutz.ioc.loader.json.JsonLoader;

public class Neo4jIocLoader extends JsonLoader {

    public Neo4jIocLoader() {
        super("org/nutz/integration/neo4j/neo4j.js");
    }
}
