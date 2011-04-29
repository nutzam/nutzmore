package org.nutz.validation.meta;

import org.nutz.validation.annotation.Validations;

/**
 * 集成注解验证的 Bean
 * 
 * @author QinerG(QinerG@gmail.com)
 */
public class Bean {

	//账号验证规则，字符串长度区间验证规则
	@Validations(account = true, strLen = { 3, 16 }, errorMsg = "account")
	private String account;

	//必填验证规则
	@Validations(required = true, errorMsg = "address")
	private String address;

	//邮编验证规则
	@Validations(post = true, errorMsg = "post")
	private String post;

	//中文验证规则
	@Validations(chinese = true, errorMsg = "name")
	private String name;

	//手机号验证规则
	@Validations(mobile = true, errorMsg = "mobile")
	private String mobile;

	//电子邮箱验证规则
	@Validations(email = true, errorMsg = "email")
	private String email;

	//QQ号验证规则
	@Validations(qq = true, errorMsg = "qq")
	private String qq;

	//字符串长度区间验证规则
	@Validations(strLen = { 5, 16 }, errorMsg = "password")
	private String password;

	//重复输入验证规则
	@Validations(repeat = "password", errorMsg = "repwd")
	private String repwd;

	//自定义验证规则，调用 checkCard 方法进行验证
	@Validations(custom = "checkCard", errorMsg = "card")
	private String card;

	//数值区间验证规则，兼容 int、long、float、double
	@Validations(limit = { 5, 80 }, errorMsg = "age")
	private int age;
	
	@Validations(el = "value*10-5<100", errorMsg = "el")
	private int power;

	// 自定义验证方法
	public boolean checkCard() {
		if ("card".equals(card)) {
			return true;
		} else {
			return false;
		}
	}
	
	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRepwd() {
		return repwd;
	}

	public void setRepwd(String repwd) {
		this.repwd = repwd;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getCard() {
		return card;
	}

	public void setCard(String card) {
		this.card = card;
	}

	public int getPower() {
		return power;
	}

	public void setPower(int power) {
		this.power = power;
	}
}
