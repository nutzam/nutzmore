var bus = new Vue({});
$(function(){
var mainSidebar = new Vue({
	el : "#main_sidebar",
	data : {
		menus : []
	},
	methods : {
		switch_page : function(path, menu_name) {
			if (localStorage) {
				localStorage.adminlte_lastmenu_page = path;
				localStorage.adminlte_lastmenu_name = menu_name;
			}
			if (!path.startsWith("/"))
				path = "/" + path;
			path = base + "/adminlte/page" + path
			if (console)
				console.info(path);
			$("#main_content").load(path);
			document.title = 'N平台-' + menu_name;
		}
	},
	created : function() {
		$.ajax({
			url : base + "/admin/hotplug/list?active=true",
			dataType : "json",
			success : function(re) {
				if (re && re.ok) {
					var menus = [];
					for (var i in re.data.list) {
						var hc = re.data.list[i];
						if (hc.menu) {
							if (console)
								console.log(hc.name, hc.menu);
							for (var k in hc.menu) {
								menus.push(hc.menu[k]);
							}
						}
					}
					mainSidebar.menus = menus;
					if (localStorage) {
						if (localStorage.adminlte_lastmenu_page) {
							mainSidebar.switch_page(localStorage.adminlte_lastmenu_page, localStorage.adminlte_lastmenu_name);
						}
					}
				} else if (re && re.message) {
					layer.alert(re.message);
				}
			}
		});
	}
});
});