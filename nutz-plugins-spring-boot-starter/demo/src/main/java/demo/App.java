package demo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.nutz.dao.ConnCallback;
import org.nutz.dao.Dao;
import org.nutz.lang.random.R;
import org.nutz.lang.util.NutMap;
import org.nutz.plugin.spring.boot.config.SqlManagerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.bean.User;
import demo.bean.User.Sex;

@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties(SqlManagerProperties.class)
@RestController
public class App {

	@RequestMapping("/")
	public SqlManagerProperties hello() {
		return sqlManagerProperties;
	}

	@RequestMapping("/dao")
	public Object dao() {
		return dao.meta();
	}

	@RequestMapping("add")
	public User add() {
		System.err.println(R.captchaChar(5));
		User u = new User();
		u.setName(String.format("(%s)", R.UU16()));
		u.setSex(Sex.FEMALE);
		u.setAccount(new BigDecimal(R.random(400, 500) * 10000));
		return dao.insert(u);
	}

	@RequestMapping("echo")
	public NutMap echo(@RequestBody NutMap data) {
		return data;
	}

	@RequestMapping("run")
	public Object run() {
		dao.run(new ConnCallback() {

			@Override
			public void invoke(Connection conn) throws Exception {
				PreparedStatement p = conn.prepareStatement("SELECT * FROM t_user");
				p.executeQuery();
				ResultSet rs = p.getResultSet();
				while (rs.next()) {
					System.err.println(rs.getInt("id"));
					System.err.println(rs.getDate("u_birth").getTime());
				}
			}
		});
		return null;
	}

	@RequestMapping("list")
	public List<User> list() {
		return dao.query(User.class, null);
	}

	@RequestMapping("/sqls")
	public Object sqls() {
		return dao.sqls().keys();
	}

	@RequestMapping("/count")
	public int count() {
		return dao.count(User.class);
	}

	@RequestMapping("dd")
	public NutMap dd() {
		return NutMap.NEW().addv("k", "(sss)");
	}

	@Autowired
	private SqlManagerProperties sqlManagerProperties;

	@Autowired
	Dao dao;

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(App.class);
		application.run(args);
	}
}
