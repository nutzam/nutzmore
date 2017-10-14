var ioc = {
    jedisqueAgent: {
        type: "org.nutz.integration.jedisque.JedisqueAgent",
        fields: {
            conf: {refer: "conf"}
        }
    },
    jedisque: {
        type: "com.github.xetorthio.jedisque.Jedisque",
        factory: "$jedisqueAgent#build",
        singleton: false
    },
    disqueService : {
    	type : "org.nutz.integration.jedisque.DisqueService",
    	fields : {
    		jedisqueAgent : {refer:"jedisqueAgent"}
    	}
    }
};