# nutz-plugins-jqgrid JQGrid dao层使用插件

简介(可用性:生产,维护者:邓华锋(http://dhf.ink))
==================================

前段jqGrid提交到后端通用处理方法：简单、易用
示例IocBy配置
----------------------------------------------
```Java
@IocBy(type=ComboIocProvider.class, args={"*js", "ioc/","*anno", "ink.dhf","org.nutz.plugins.jqgrid"})
```
										   
前段jqGrid配置：
```javascript
jQuery(grid_selector).jqGrid({
	url:"${base}/user/bean/list",//${base}/user/tname/list
	datatype: "json",
	......省略
	editurl: "${basePath}/user/oper",//nothing is saved
});
```

```javascript
$('#btnSeach').on('click', function(e){
	//此处可以添加对查询数据的合法验证    
	var userId = $("#btnUser").val();    
	$(grid_selector).jqGrid('setGridParam',{    
	   datatype:'json',    
	   postData:{'userId':userId},  
	   page:1    
	}).trigger("reloadGrid");  
});
```
module使用示例：
```Java
@At("/user")
@IocBean
public class UserModule {
	@Inject
	JQGridService jQGridService;

	@At("/bean/list")
	@Fail(">>:/index")
	@Ok("json")
	public JQGridResult beanList(Integer userId, @Param("..") JQGridPage jqGridPage) {
		Cnd cnd = Cnd.where("1", "=", 1);
		if(userId!=null&&userId>0) {
			cnd.and("userId", "=", userId);
		}
		Dao dao = Mvcs.getIoc().get(Dao.class);
		return jQGridService.query(jqGridPage, dao, cnd, "id", TestUser.class);
	}


	@At("/tname/list")
	@Fail(">>:/index")
	@Ok("json")
	public JQGridResult tableNamelist(Integer userId, @Param("..") JQGridPage jqGridPage) {
		Cnd cnd = Cnd.where("1", "=", 1);
		if(userId!=null&&userId>0) {
			cnd.and("userId", "=", userId);
		}
		Dao dao = Mvcs.getIoc().get(Dao.class);
		return jQGridService.query(jqGridPage, "test_user", dao, cnd, "id");
	}


	@At
	@Ok("json")
	public Object oper(@Param("..") final Map<String, Object> data, @Param("oper") String oper) {
		if (StringUtils.equals(oper, JQGridOper.add.name())) {
			//TODO add
			return true;
		}else if (StringUtils.equals(oper, JQGridOper.del.name())) {
			//TODO delete
			return true;
		}else if(StringUtils.equals(oper, JQGridOper.edit.name())) {
			//TODO update
			return true;
		}
		return false;
	}
} 
```
