var ioc = {
    zk_factory : {
    	type : "org.apache.zookeeper.ZooKeeperFactory",
    	fields : {
    		conf : {refer:"conf"},
    		ioc : {refer:"$ioc"}
    	}
    },
	zk : {
		type : "org.apache.zookeeper.ZooKeeper",
		factory : "$zk_factory#get"
	}
};