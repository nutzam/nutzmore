package org.nutz.dao.convent.orm;

public interface IOrmRule {
	/**
	 * 类名转表�?
	 * @param className 类名
	 * @return 表名
	 */
	public String class2TableName(String className);
	/**
	 * 表名转类�?
	 * @param tableName 表名
	 * @return 类名
	 */
	public String tableName2ClassName(String tableName);
	/**
	 * java中属性转数据库中列名
	 * @param javaField java中属性名
	 * @return 数据库中列名
	 */
	public String javaField2DbField(String javaField);
	/**
	 * 数据库中的列名转java中的属�?�名
	 * @param dbField 数据库中的列�?
	 * @return java中的属�?�名
	 */
	public String dbField2JavaField(String dbField);
}
