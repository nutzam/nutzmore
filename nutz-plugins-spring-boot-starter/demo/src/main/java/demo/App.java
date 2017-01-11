package demo;

import org.nutz.dao.Dao;
import org.nutz.plugin.spring.boot.config.SqlManagerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.bean.User;

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

	@RequestMapping("/sqls")
	public Object sqls() {
		return dao.sqls().keys();
	}

	@RequestMapping("/count")
	public int count() {
		return dao.count(User.class);
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
