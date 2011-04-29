package org.nutz.validation;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.validation.annotation.AnnotationValidation;
import org.nutz.validation.meta.Bean;

/**
 * 12 种验证规则及组合规则的测试
 * 
 * @author QinerG(QinerG@gmail.com)
 */
public class ValidationTest {
	
	private Bean b = new Bean();
	private AnnotationValidation av = new AnnotationValidation();
	
	public void init() {
		b.setAccount("dsaf_kp");
		b.setEmail("aaa@aa.com");
		b.setMobile("13699999999");
		b.setQq("213131");
		b.setName("弓勤");
		b.setPost("100192");
		b.setAge(10);
		b.setPassword("sadf1");
		b.setRepwd("sadf1");
		b.setCard("card");
		b.setAddress("天安门");
		b.setPower(10);
	}

	@Test
	public void testAll() {
		init();
		assertFalse(av.validate(b).hasError());
	}
	
	@Test
	public void testAccount() {
		init();
		//只能是英文开头
		b.setAccount("中文开头");
		assertEquals(1, av.validate(b).errorCount());
		//不允许有符号
		b.setAccount("g@poq_f");
		assertEquals(1, av.validate(b).errorCount());
		//长度必须在 3-6　位
		b.setAccount("ab");
		assertEquals(1, av.validate(b).errorCount());
		//正确的值
		b.setAccount("abc");
		assertFalse(av.validate(b).hasError());
	}
	
	@Test
	public void testRequired() {
		init();
		b.setAddress("");
		assertEquals(1, av.validate(b).errorCount());
		b.setAddress(null);
		assertEquals(1, av.validate(b).errorCount());
	}
	
	@Test
	public void testPost() {
		init();
		b.setPost("12312312");
		assertEquals(1, av.validate(b).errorCount());
		b.setPost("12345");
		assertEquals(1, av.validate(b).errorCount());
	}
	
	@Test
	public void testChinese() {
		init();
		b.setName("12中文");
		assertEquals(1, av.validate(b).errorCount());
	}
	
	@Test
	public void testMobile() {
		init();
		b.setMobile("98765432101");
		assertEquals(1, av.validate(b).errorCount());
	}
	
	@Test
	public void testEmail() {
		init();
		b.setEmail("aa@c");
		assertEquals(1, av.validate(b).errorCount());
	}
	
	@Test
	public void testQq() {
		init();
		b.setQq("1234");
		assertEquals(1, av.validate(b).errorCount());
	}
	
	@Test
	public void testStrLen() {
		init();
		b.setPassword("1234");
		b.setRepwd("1234");
		assertEquals(1, av.validate(b).errorCount());
		b.setPassword("12345678901234567");
		b.setRepwd("12345678901234567");
		assertEquals(1, av.validate(b).errorCount());
	}
	
	@Test
	public void testRepwd() {
		init();
		b.setPassword("12345");
		b.setRepwd("54321");
		assertEquals(1, av.validate(b).errorCount());
		b.setPassword("12345");
		b.setRepwd(null);
		assertEquals(1, av.validate(b).errorCount());
	}
	
	@Test
	public void testEl() {
		init();
		b.setPower(10);
		assertEquals(0, av.validate(b).errorCount());
		b.setPower(11);
		assertEquals(1, av.validate(b).errorCount());
	}
	
	@Test
	public void testCustom() {
		init();
		b.setCard("card1");
		assertEquals(1, av.validate(b).errorCount());
	}
	
	@Test
	public void testLimit() {
		init();
		b.setAge(4);
		assertEquals(1, av.validate(b).errorCount());
		b.setAge(5);
		assertEquals(0, av.validate(b).errorCount());
		b.setAge(81);
		assertEquals(1, av.validate(b).errorCount());
	}
	
}
