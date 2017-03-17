package demo.bean;

import java.math.BigDecimal;
import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.lang.Times;

@Table("t_user")
public class User {

	@Id
	private int id;

	@Column("u_name")
	@Name
	private String name;


	@Column("u_birth")
	private Date brithDay = Times.now();

	@Column("u_sex")
	private Sex sex;

	@Column("u_account")
	@ColDefine(customType = "decimal(65,10)")
	private BigDecimal account;


	/**
	 * @return the account
	 */
	public BigDecimal getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(BigDecimal account) {
		this.account = account;
	}

	public static enum Sex {
		MALE, FEMALE
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBrithDay() {
		return brithDay;
	}

	public void setBrithDay(Date brithDay) {
		this.brithDay = brithDay;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		this.sex = sex;
	}

}
