package demo.biz;

import javax.annotation.PostConstruct;

import org.nutz.lang.Lang;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.plugin.spring.boot.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import demo.bean.T;

@Service
public class TService extends BaseService<demo.bean.T> {

	public static void main(String[] args) {
		System.err.println(Times.D("0000-00-00 00:00:00"));
	}

	@PostConstruct
	public void init() {
		dao().create(getEntityClass(), false);
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
