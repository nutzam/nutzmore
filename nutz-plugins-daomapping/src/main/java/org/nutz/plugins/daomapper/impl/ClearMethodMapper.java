package org.nutz.plugins.daomapper.impl;

import java.lang.reflect.Method;

import org.nutz.dao.Dao;
import org.nutz.plugins.daomapper.MethodMapper;

public class ClearMethodMapper extends MethodMapper {

	public ClearMethodMapper(Dao dao, Method method, String pName) {
		super(dao, method, pName);
	}

	public Object exec(Object[] args) {
		return dao.clear(pojoType, makeCnd(args));
	}

	public String prefix() {
		return "clear";
	}

}
