var vueClientList = new Vue({
	el : "#client_manager_div",
	data : {
		clients : [],
		pager : {pageNumber:1,pageCount:1}
	},
	methods : {
		dataReload : function() {
			$.ajax({
		    	url : base + "/ngrokd/client/query",
		    	dataType : "json",
		    	data : "pageSize=10&pageNumber="+this.pager.pageNumber,
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vueClientList.clients = re.data.list;
		    			vueClientList.pager = re.data.pager;
		    		} else if (re && re.msg) {
						layer.alert(re.msg);
					}
		    	},
		    	fail : function(err) {
		    		layer.alert("加载失败:" + err);
		    	},
		    	error : function (err){
		    		layer.alert("加载失败:" + err);
		    	}
		    });
		},
	    changePage: function(to_page) {
	    	this.pager.pageNumber = to_page;
	    	this.dataReload();
	    },
	    client_kill : function (client_id) {
	    	$.ajax({
		    	url : base + "/ngrokd/client/kill",
		    	dataType : "json",
		    	data : {id:client_id},
		    	success : function(re) {
		    		if (console)
		    			console.info(re);
		    		if (re && re.ok) {
		    			vueClientList.dataReload();
		    		} else if (re && re.msg) {
						layer.alert(re.msg);
					}
		    	},
		    	fail : function(err) {
		    		layer.alert("加载失败:" + err);
		    	},
		    	error : function (err){
		    		layer.alert("加载失败:" + err);
		    	}
		    });
		},
	},
	created: function () {
	    this.dataReload();
    }
});