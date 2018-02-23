package org.nutz.plugins.jqgrid.entity;
import java.io.Serializable;


/**
 * jqGrid规则
 * 
 * @author 邓华锋
 * @date 2016年6月27日 下午4:21:21
 *
 */
public class JQGridRule implements Serializable{
	private static final long serialVersionUID = -2672331350537972005L;
	
	private String field;
	
    private String op;
    
    private String data;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
