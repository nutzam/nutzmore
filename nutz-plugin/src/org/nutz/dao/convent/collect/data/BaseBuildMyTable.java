package org.nutz.dao.convent.collect.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.convent.tools.pojo.MyField;
import org.nutz.dao.convent.utils.DataBaseHelper;


public class BaseBuildMyTable {
	/**
	 * 创建插入语句
	 * @param tableName 表名
	 * @param fields 字段
	 * @return insert语句
	 */
	public String buildInsertSql(String tableName,MyField[] fields){
		String front="insert into "+tableName;
		String sql=" values(";
		//Field[] fields=this.getTableField(tableName);
		String tempSql="(";
		for(int i=0;i<fields.length;i++){
			sql=sql+"?";
			tempSql=tempSql+fields[i].getFieldName();
			if(i!=fields.length-1){
				sql=sql+",";
				tempSql=tempSql+",";
			}
			
		}
		sql=sql+")";
		tempSql=tempSql+")";
		front=front+tempSql;
		return front+sql;
	}
	/**
	 * 根据主键更新的update语句
	 * @param tableName 表名
	 * @param fields 字段
	 * @return update语句
	 */
	public String buildUpdateSql(String tableName,MyField[] fields){
		String sql="update "+tableName+" set ";
//		Field[] fields=this.getTableField(tableName);
		String sql1="";//非主键拼接的sql
		String sql2=" where 1=1 ";//主键拼接的sql
		for(int i=0;i<fields.length;i++){
			if(!fields[i].isKey()){
				sql1=sql1+fields[i].getFieldName()+"=?,";
			}else{
				sql2=sql2+" and "+fields[i].getFieldName()+"=? ";
			}
		}
		if(sql1.length()>1){
			sql1=sql1.substring(0, sql1.length()-1);//截取最后一个逗号
		}
		sql=sql+sql1+sql2;
		return sql;
	}
	/**
	 * 根据主键删除的delete语句
	 * @param tableName 表名
	 * @param fields 字段
	 * @return delete语句
	 */
	public String buildDeleteSql(String tableName,MyField[] fields){
		String sql="delete from "+tableName+" where 1=1 ";
//		Field[] fields=this.getTableField(tableName);
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].isKey()){
				sql=sql+" and "+fields[i].getFieldName()+"=? ";
			}
		}
		return sql;
	}
	public String buildSelectSql(String tableName,MyField[] fields){ 
		String sql="select * from "+tableName+" where 1=1 ";
		for (int i = 0; i < fields.length; i++) {
			if(fields[i].isKey()){
				sql=sql+" and "+fields[i].getFieldName()+"=? ";
			}
		}
		return sql;
	}
	@SuppressWarnings("unchecked")
	public Map getForeignKey(String tableName,Connection conn) {
		Map foreignMap=new HashMap();
		ResultSet rs=null;
		try {
			DatabaseMetaData metaData=conn.getMetaData();
			rs=metaData.getImportedKeys(conn.getCatalog(), metaData.getUserName(), tableName);
			
			while(rs.next()){
				String pkTableName=rs.getString("PKTABLE_NAME");
				String pkColumnName=rs.getString("PKCOLUMN_NAME");
				String fkTableName=rs.getString("FKTABLE_NAME");
				String fkColumnName=rs.getString("FKCOLUMN_NAME");
				foreignMap.put(fkTableName+"."+fkColumnName, pkTableName+"."+pkColumnName);
			}
			
		} catch (SQLException e) {
			//这里出现的异常怎么办呢?
		} finally{
			DataBaseHelper.closeRs(rs);
			DataBaseHelper.closeConn(conn);
		}
		return foreignMap; 
	}
}
