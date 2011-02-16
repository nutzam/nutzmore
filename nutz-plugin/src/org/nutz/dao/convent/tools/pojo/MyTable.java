/**
 * 
 */
package org.nutz.dao.convent.tools.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * Title:功能描述
 * Description:功能描述
 * </pre>
 * @author liaohongliu liaohl@yuchengtech.com
 * @version 1.0   2009-7-29
 * 
 * <pre>
 * 修改记录
 * 	  修改后版本:      修改人:     修改时间:       修改内容
 * </pre>
 */
public class MyTable {
	private String tableChineseName;//表的中文名
	private String tableName;//表名
	private MyField[] fields;//表对应的字段
	private String insertSql;//插入一条记录的sql
	private String updateSql;//以主键为更新条件的sql
	private String deleteSql;//以主键为删除条件的sql
	private String selectSql;//以主键为条件的查询sql

	public String getSelectSql() {
		return selectSql;
	}
	public void setSelectSql(String selectSql) {
		this.selectSql = selectSql;
	}
	public MyField[] getFields() {
		return fields;
	}
	public List<MyField> getFieldList() {
		if(fields==null||fields.length==0){
			throw new RuntimeException("表的字段对象数组为空,请检查其正确性!");
		}
		List<MyField> fieldList=new ArrayList<MyField>();
		for(int i=0;i<fields.length;i++){
			fieldList.add(fields[i]);
		}
		return fieldList;
	}
	public boolean containField(String fieldName){
		List<MyField> fields=this.getFieldList();
		for (MyField field : fields) {
			if(field.getFieldName().equals(fieldName)){
				return true;
			}
		}
		return false;
	}
	public MyField getField(String fieldName){
		List<MyField> fields=this.getFieldList();
		for (MyField field : fields) {
			if(field.getFieldName().equals(fieldName)){
				return field;
			}
		}
		return null;
	}
	public List<MyField> getPkFields(){
		List<MyField> pkFields=new ArrayList<MyField>();
		List<MyField> fields=this.getFieldList();
		for (MyField field : fields) {
			if(field.isKey()){
				pkFields.add(field);
			}
		}
		return pkFields;
	}
	public void setFields(MyField[] fields) {
		this.fields = fields;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public MyTable() {
		// TODO 自动生成构造函数存根
	}
	public MyTable(String tableId, String tableChineseName) {
		super();
//		this.tableId = tableId;
		this.tableChineseName = tableChineseName;
	}
	public String getTableChineseName() {
		return tableChineseName;
	}
	public MyTable setTableChineseName(String tableChineseName) {
		this.tableChineseName = tableChineseName;
		return this;
	}
	public String getDeleteSql() {
		return deleteSql;
	}
	public void setDeleteSql(String deleteSql) {
		this.deleteSql = deleteSql;
	}
	public String getInsertSql() {
		return insertSql;
	}
	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}
	public String getUpdateSql() {
		return updateSql;
	}
	public void setUpdateSql(String updateSql) {
		this.updateSql = updateSql;
	}
}
