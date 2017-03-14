var ioc = {
	$aop_slog : {
		type : "org.nutz.plugins.slog.aop.SlogAopConfigration"
	},
    slogService : {		
    	type : "org.nutz.plugins.slog.service.SlogService",		
    	fields : {		
    		dao : {refer:"dao"}		
    	}		
    }
}