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
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.bind.annotation.GetMapping;
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

	public static class DTO {
		private boolean fb;
		private Boolean fb1;

		private short s;
		private Short s1;

		private byte b;
		private Byte b1;

		private int i;
		private Integer i1;

		private long l;
		private Long l1;

		private float f;
		private Float f1;

		private double d;
		private Double d1;

		private char c;
		private Character c1;

		public boolean isFb() {
			return fb;
		}

		public void setFb(boolean fb) {
			this.fb = fb;
		}

		public Boolean getFb1() {
			return fb1;
		}

		public void setFb1(Boolean fb1) {
			this.fb1 = fb1;
		}

		public short getS() {
			return s;
		}

		public void setS(short s) {
			this.s = s;
		}

		public Short getS1() {
			return s1;
		}

		public void setS1(Short s1) {
			this.s1 = s1;
		}

		public byte getB() {
			return b;
		}

		public void setB(byte b) {
			this.b = b;
		}

		public Byte getB1() {
			return b1;
		}

		public void setB1(Byte b1) {
			this.b1 = b1;
		}

		public int getI() {
			return i;
		}

		public void setI(int i) {
			this.i = i;
		}

		public Integer getI1() {
			return i1;
		}

		public void setI1(Integer i1) {
			this.i1 = i1;
		}

		public long getL() {
			return l;
		}

		public void setL(long l) {
			this.l = l;
		}

		public Long getL1() {
			return l1;
		}

		public void setL1(Long l1) {
			this.l1 = l1;
		}

		public float getF() {
			return f;
		}

		public void setF(float f) {
			this.f = f;
		}

		public Float getF1() {
			return f1;
		}

		public void setF1(Float f1) {
			this.f1 = f1;
		}

		public double getD() {
			return d;
		}

		public void setD(double d) {
			this.d = d;
		}

		public Double getD1() {
			return d1;
		}

		public void setD1(Double d1) {
			this.d1 = d1;
		}

		public char getC() {
			return c;
		}

		public void setC(char c) {
			this.c = c;
		}

		public Character getC1() {
			return c1;
		}

		public void setC1(Character c1) {
			this.c1 = c1;
		}

	}

	@GetMapping("json")
	public NutMap json() {
		return NutMap.NEW().addv("status", 0).addv("d", new DTO());
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
		application.addListeners(new ApplicationListener<ContextRefreshedEvent>() {

			@Override
			public void onApplicationEvent(ContextRefreshedEvent event) {
				System.err.println(1);
			}
		});
		application.run(args);
	}
}
