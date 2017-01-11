package demo.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("t_user")
public class User {

	@Id
	private int id;

	@Column("u_name")
	private String name;

	@Column("u_birth")
	private Date brithDay;

	@Column("u_sex")
	private Sex sex;

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
