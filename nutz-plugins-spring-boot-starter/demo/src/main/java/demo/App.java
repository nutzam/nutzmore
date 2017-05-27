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
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import demo.bean.User;
import demo.bean.User.Sex;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties(SqlManagerProperties.class)
@RestController
@EnableSwagger2
public class App {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2)
				.genericModelSubstitutes(DeferredResult.class)
				.useDefaultResponseMessages(false)
				.forCodeGeneration(true)
				.pathMapping("/")
				.select()
				.apis(RequestHandlerSelectors.basePackage("demo"))
				.build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("AD Platform API")
				.description("AUTO-DEPLOY 接口手册")// 详细描述
				.version("1.0")// 版本
				.termsOfServiceUrl("ZHCS.CLUB")
				.contact(new Contact("王贵源", "https://git.oschina.net/Edgware/auto-deploy.git", "wangguiyuan@sinosoft.com.cn"))// 作者
				.license("The Apache License, Version 2.0")
				.licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
				.build();
	}

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
		return dao.sqls();
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
