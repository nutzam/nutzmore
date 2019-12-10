package demo.biz;

import javax.annotation.PostConstruct;

import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Lang;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.plugin.spring.boot.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import demo.bean.T;

@Service
public class TService extends BaseService<demo.bean.T> {


	@PostConstruct
	public void init() {
		dao().create(getEntityClass(), false);
	}
	
	public int testSqlTemp() {
		Sql sql = create("test.tpl");
		sql.vars().set("a", R.random(1, 10));
		sql.params().set("b", R.random(1, 10));
		sql.setCallback(Sqls.callback.integer());
		dao().execute(sql);
		return sql.getInt();
	}

	@Transactional
	public NutMap test() {
		for (int i = 0; i < 10; i++) {
			save(new T());
		}
		if (R.random(0, 10) <= 8) {
			throw Lang.makeThrow("test throw %d", Thread.currentThread().getId());
		}
		return NutMap.NEW();
	}

}
