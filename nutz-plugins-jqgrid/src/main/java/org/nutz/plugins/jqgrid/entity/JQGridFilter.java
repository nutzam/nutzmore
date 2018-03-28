package org.nutz.plugins.jqgrid.entity;
import java.io.Serializable;
import java.util.List;

/**
 * jqGrid过滤器
 * 
 * @author 邓华锋
 * @date 2016年6月27日 下午4:22:41
 *
 */
public class JQGridFilter implements Serializable{
	
	private static final long serialVersionUID = 3777917034270668946L;
	
	private String groupOp;
	
    private List<JQGridRule> rules;

	public String getGroupOp() {
		return groupOp;
	}

	public void setGroupOp(String groupOp) {
		this.groupOp = groupOp;
	}

	public List<JQGridRule> getRules() {
		return rules;
	}

	public void setRules(List<JQGridRule> rules) {
		this.rules = rules;
	}
}