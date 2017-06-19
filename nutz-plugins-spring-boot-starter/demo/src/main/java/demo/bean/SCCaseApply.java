package demo.bean;

import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Comment;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Table;

@Table("afdi_sc_case_appl")
@Comment("安全调查案件和申请单关系")
public class SCCaseApply {

	@Id
	@Column("afdi_sc_case_appl_key")
	@Comment("自增id")
	private int id;

	@Column("afdi_sc_case_key")
	@Comment("案件id")
	private int caseId;

	@Column("afdi_application_key")
	@Comment("申请单id")
	private int applId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCaseId() {
		return caseId;
	}

	public void setCaseId(int caseId) {
		this.caseId = caseId;
	}

	public int getApplId() {
		return applId;
	}

	public void setApplId(int applId) {
		this.applId = applId;
	}

}
