var ioc = {
	conf : {
		type : "org.nutz.ioc.impl.PropertiesProxy",
		fields : {
			paths : ["custom/"]
		}
	}
	dataSource : {
		type : "org.nutz.dao.impl.SimpleDataSource",
		fields : {
			jdbcUrl : {java:"$conf.get('db.jdbcUrl')"}
		}
	}	
};