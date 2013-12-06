package org.nutz.plugins.hbm;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.json.Json;
import org.nutz.plugins.hbm.bean.Employee;

public class HbmEntityMakerTest {

	private HbmEntityMaker maker = new HbmEntityMaker();
	
	@Test
	public void testAddHbmStream() {
		maker.setPaths(getClass().getPackage().getName().replace('.', '/'));
		System.out.println(Json.toJson(maker.make(Employee.class)));
	}

}
