package org.nutz.plugins.jqgrid.service;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.json.Json;
import org.nutz.plugins.jqgrid.BaseNutTest;
import org.nutz.plugins.jqgrid.entity.JQGridPage;
import org.nutz.plugins.jqgrid.entity.JQGridResult;
import org.nutz.plugins.jqgrid.entity.TestUser;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestJQGridService extends BaseNutTest {
	@Inject
	Dao dao;
	@Inject
	JQGridService jQGridService;
	@Test
	public void testQueryBeanList() {
		JQGridPage jqGridPage=new JQGridPage();
		Cnd cnd = Cnd.where("1", "=", 1);
		JQGridResult JQGridResult=jQGridService.query(jqGridPage, dao, cnd, "id", TestUser.class);
		System.out.println(Json.toJson(JQGridResult));
	}
	
	@Test
	public void testQueryTableNameList() {
		JQGridPage jqGridPage=new JQGridPage();
		Cnd cnd = Cnd.where("1", "=", 1);
		JQGridResult JQGridResult=jQGridService.query(jqGridPage, "test_user", dao, cnd, "id");
		System.out.println(Json.toJson(JQGridResult));
	}
}
