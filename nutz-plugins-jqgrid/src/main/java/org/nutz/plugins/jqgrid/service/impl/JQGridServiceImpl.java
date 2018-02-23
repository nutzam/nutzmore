package org.nutz.plugins.jqgrid.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Criteria;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Strings;
import org.nutz.plugins.jqgrid.dict.JQGridGroupOP;
import org.nutz.plugins.jqgrid.dict.JQGridOrder;
import org.nutz.plugins.jqgrid.dict.JQGridSelectOPT;
import org.nutz.plugins.jqgrid.entity.JQGridPage;
import org.nutz.plugins.jqgrid.entity.JQGridResult;
import org.nutz.plugins.jqgrid.entity.JQGridRule;
import org.nutz.plugins.jqgrid.service.JQGridService;
import org.nutz.plugins.jqgrid.util.Args;

/**
 * 操作jqgrid service类
 * @author 邓华锋 http://dhf.ink
 *
 */
@IocBean(name = "jQGridService")
public class JQGridServiceImpl implements JQGridService {
	private static Map<String, String> selectOPT = new HashMap<String, String>();

	static {
		// 遍历JQGrid查询操作符枚举及对应的操作符，放入到静态变量中
		for (JQGridSelectOPT sopt : JQGridSelectOPT.values()) {
			selectOPT.put(sopt.name(), sopt.value());
		}
	}

	@Override
	public JQGridResult query(JQGridPage jqGridPage, String tableName, Dao dao, Cnd cnd, String defaultOrderField) {
		Args.notBlank(tableName, "tableName");
		return query(jqGridPage, tableName, dao, cnd, defaultOrderField, null);
	}

	/**
	 * JQGrid通用查询
	 * 
	 * @param jqGridPage
	 * @param dao
	 * @param cnd
	 * @param defaultOrderField
	 * @param clazz
	 * @return
	 */
	public JQGridResult query(JQGridPage jqGridPage, Dao dao, Cnd cnd, String defaultOrderField, Class<?> clazz) {
		Args.notNull(clazz, "clazz");
		return query(jqGridPage, null,dao, cnd, defaultOrderField, clazz);
	}

	private JQGridResult query(JQGridPage jqGridPage, String tableName, Dao dao, Cnd cnd, String defaultOrderField,
			Class<?> clazz) {
		Args.notNull(jqGridPage, "jqGridPage");
		Args.notNull(dao, "dao");
		Args.notNull(cnd, "cnd");
		if (jqGridPage.getRows() < 1)
			jqGridPage.setRows(10);

		Pager pager = dao.createPager(jqGridPage.getPage(), jqGridPage.getRows());
		// 创建一个 Criteria 接口实例
		Criteria cri = null;
		if (cnd != null) {
			cri = cnd.getCri();
		} else {
			cri = Cnd.cri();
		}

		if (jqGridPage.is_search() && jqGridPage.getFilters() != null) {
			String groupOp = jqGridPage.getFilters().getGroupOp().toLowerCase();
			List<JQGridRule> rules = jqGridPage.getFilters().getRules();
			if (rules != null && rules.size() > 0) {
				if (Strings.equals(groupOp, JQGridGroupOP.and.name())) {
					for (JQGridRule rule : rules) {
						String opt = selectOPT.get(rule.getOp());
						String data = rule.getData();
						String field = rule.getField();
						opt = opt.toLowerCase();
						if (opt.startsWith("like")) {
							String vd = opt.replace("like ", "");
							String vdata = vd.replace("${value}", data);
							// data="%"+data+"%";
							// "%A%"
							cri.where().andLike(field, vdata);
						} else if (opt.equals("in")) {
							cri.where().andIn(field, data);
						} else if (opt.equals("not in")) {
							cri.where().andNotIn(field, data);
						} else {
							cri.where().and(field, opt, data);
						}
					}
				} else if (Strings.equals(groupOp, JQGridGroupOP.or.name())) {
					for (JQGridRule rule : rules) {
						String opt = selectOPT.get(rule.getOp());
						String data = rule.getData();
						String field = rule.getField();
						opt = opt.toLowerCase();
						if (opt.startsWith("like")) {
							String vd = opt.replace("like ", "");
							String vdata = vd.replace("${value}", data);
							// data="%"+data+"%";
							// "%A%"
							cri.where().orLike(field, vdata);
						} else if (opt.equals("in")) {
							cri.where().orIn(field, data);
						} else if (opt.equals("not in")) {
							cri.where().orNotIn(field, data);
						} else {
							cri.where().or(field, opt, data);
						}
					}
				}
			}
		}

		if (!Strings.isEmpty(jqGridPage.getSidx()) && !Strings.isEmpty(jqGridPage.getSord())) {
			if (Strings.equals(jqGridPage.getSord(), JQGridOrder.asc.name())) {
				cri.getOrderBy().asc(jqGridPage.getSidx());
			} else if (Strings.equals(jqGridPage.getSord(), JQGridOrder.desc.name())) {
				cri.getOrderBy().desc(jqGridPage.getSidx());
			}
		} else {
			cri.getOrderBy().desc(defaultOrderField);
		}
		List<?> list = null;
		if (Strings.isBlank(tableName)) {
			list = dao.query(clazz, cri, pager);
		} else {
			list = dao.query(tableName, cri, pager);
		}

		if (pager != null) {
			if (Strings.isBlank(tableName)) {
				pager.setRecordCount(dao.count(clazz, cnd));
			} else {
				pager.setRecordCount(dao.count(tableName, cnd));
			}

		}
		return new JQGridResult(pager, list);
	}
}
