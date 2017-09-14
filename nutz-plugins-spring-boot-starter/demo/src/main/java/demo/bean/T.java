package demo.bean;

import java.util.Date;

import org.nutz.dao.entity.annotation.ColDefine;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.json.JsonField;
import org.nutz.lang.Times;
import org.nutz.lang.random.R;
import org.nutz.plugin.spring.boot.service.entity.DataBaseEntity;

@Table("t_tt_ttt_tttt")
public class T extends DataBaseEntity {

	@Id
	long id;

	@Name
	String uuid = R.UU64();

	@Column("long_text")
	@ColDefine(width = 2000)
	@JsonField(ignore = true)
	String longText = R.sg(20).next();

	@Column("t_tt_time")
	@JsonField(dataFormat = "yyyy-MM-dd HH:mm:ss")
	Date date = Times.now();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getLongText() {
		return longText;
	}

	public void setLongText(String longText) {
		this.longText = longText;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
