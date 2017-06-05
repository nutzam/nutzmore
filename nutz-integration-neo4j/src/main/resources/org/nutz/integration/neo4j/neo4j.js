var ioc = {
		neo4jd : {
			type : "org.neo4j.driver.v1.Driver",
			factory : "org.nutz.integration.neo4j.Neo4jFactory#build",
			args : [{refer:"conf"}, "neo4j."]
		},
		neo4j : {
			type : "org.nutz.integration.neo4j.Neo4jInterceptor",
			fields : {
				driver : {refer:"neo4jd"}
			}
		}
}