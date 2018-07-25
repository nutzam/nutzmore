var ioc = {
		// 参考 https://github.com/xetorthio/jedis/wiki/Getting-started
		jedisPoolConfig : {
			type : "redis.clients.jedis.JedisPoolConfig",
			factory : "$conf#make",
			args : ["redis.clients.jedis.JedisPoolConfig", "redis.pool."],
			fields : {
				maxTotal : {java : "$conf.getInt('redis.maxTotal', 100)"}, // 一般都够了吧
			}
		},
		jedisPool : {
			type : "redis.clients.jedis.JedisPool",
			args : [
			        {refer : "jedisPoolConfig"},
			        // 从配置文件中读取redis服务器信息
			        {java : "$conf.get('redis.host', 'localhost')"}, 
			        {java : "$conf.getInt('redis.port', 6379)"}, 
			        {java : "$conf.getInt('redis.timeout', 2000)"}, 
			        {java : "$conf.get('redis.password')"}, 
			        {java : "$conf.getInt('redis.database', 0)"}
			        ],
			fields : {},
			events : {
				depose : "destroy" // 关闭应用时必须关掉呢
			}
		},
		jedisClusterNodes : {
			type : "org.nutz.integration.jedis.JedisClusterNodeSet",
			fields : {
                nodes:{java : "$conf.get('redis.nodes')"},
                host:{java : "$conf.get('redis.host', 'localhost')"},
                port:{java : "$conf.getInt('redis.port', 6379)"}
			},
			events : {
				create : "init"
			}
		},
		jedisCluster : {
			type : "redis.clients.jedis.JedisCluster",
			args : [
				{refer:"jedisClusterNodes"},
                {java : "$conf.getInt('redis.timeout', 2000)"},
                {java : "$conf.getInt('redis.soTimeout', 0)"},
				{java : "$conf.getInt('redis.max_redir', 10)"},
		        {java : "$conf.get('redis.password')"}, 
				{refer:"jedisPoolConfig"}
			],
			events : {
				depose : "close"
			}
		},
		redis : {
			type : "org.nutz.integration.jedis.RedisInterceptor",
			fields : {
				jedisAgent : {refer:"jedisAgent"}
			}
		},
		redisService : {
			type : "org.nutz.integration.jedis.RedisService",
			fields : {
				jedisAgent : {refer:"jedisAgent"}
			}
		},
		pubSubService : {
			type : "org.nutz.integration.jedis.pubsub.PubSubService",
			fields : {
				jedisAgent : {refer:"jedisAgent"}
			},
			events : {
				depose : "depose"
			}
		},
		jedisAgent : {
			type : "org.nutz.integration.jedis.JedisAgent",
			fields : {
				ioc : {refer:"$ioc"},
				conf : {refer:"conf"}
			}
		},
		jedisClusterWrapper : {
			type : "org.nutz.integration.jedis.JedisClusterWrapper",
			args : [{refer:"jedisCluster"}]
		}
};