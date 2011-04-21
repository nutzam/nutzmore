/**
 * 
 */
package org.nutz.dao.convent.utils;

import java.io.Closeable;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author user
 *
 */
public class CommonUtils {

	public static final SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat dateTimeFormatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat tableFormatter=new SimpleDateFormat("yyMMdd");
	/**
	 * 将Strng类型的值转换为相应的类型
	 * @param value String类型的值
	 * @param toType 转换的类型
	 * @return 转换类型后的值
	 */
	public static Object getValueFromString(String value,Class toType){
		if(toType==Double.class){
			return new Double(value);
		}else if(toType==Integer.class){
			return new Integer(value);
		}else if(toType==Long.class){
			return new Long(value);
		}else if(toType==java.util.Date.class){
			return java.sql.Date.valueOf(value);
		}else{
			return value;
		}
	}
	/**
	 * 判断字符串的有效性
	 * @param str 要判断的字符串
	 * @return 是否有效,true:有效
	 */
	public static boolean isValidString(String str){
		return str!=null&&str.trim().length()>0;
	}
	public static boolean isValidNum(String str){
		if(str==null || "".equals(str.trim())){
			return false;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str.trim());
		if(isNum.matches()){
			return true;
		}else{
			return false;
		} 
	}
	/**
	 * 得到多个小数点格式的字符串的最后一个
	 * @param name 字符串
	 * @return 截取后的字符串
	 */
	public static String getSimpleName(String name){
		if(name.lastIndexOf(".")!=-1){
			String temp=name.substring(name.lastIndexOf(".")+1);
			return temp;
		}else{
			return name;
		}
	}
	/**
	 * 得到多个小数点格式的字符串的最开始一个
	 * @param name 字符串
	 * @return 截取后的字符串
	 */
	public static String getFirstName(String name){
		if(name.indexOf(".")!=-1){
			String temp=name.substring(0,name.indexOf("."));
			return temp;
		}else{
			return name;
		}
	}
	/**
	 * 根据字段名来获得字段,支持两级
	 * @param clazz 指定要获得字段的类
	 * @param name 字段名,多级用点号隔开
	 * @return 找到的字段,如果没有就为空
	 */
	public static Field getFieldByString(Class clazz,String name){
		try {
			if(name.indexOf(".")==-1){
				return clazz.getDeclaredField(name);
			}
			Field field=clazz.getDeclaredField(name.substring(0,name.indexOf(".")));
			Class cla=field.getType();
			return cla.getDeclaredField(name.substring(name.indexOf(".")+1));
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * 让首字母大写
	 * @param str 要大写的单词
	 * @return 首字母大写后的单词
	 */
	public static String getFirstUpper(String str){
		return str.substring(0, 1).toUpperCase()+str.substring(1);
	}
	public static void closeStream(Closeable steam){
		try {
			if(steam!=null){
				steam.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getYear(String inputDate){
		String[] datas=inputDate.split("-");
		return datas[0];
	}
	public static String getMonth(String inputDate){
		String[] datas=inputDate.split("-");
		return datas[1];
	}
	public static String getDay(String inputDate){
		String[] datas=inputDate.split("-");
		return datas[2];
	}
	public static Date getDayFromStr(String inputDate){
		return java.sql.Date.valueOf(inputDate);
	}
	public static List<File> getFilesBySuffix(String dir, String... suffixs) {
		File file = new File(dir);
		List<File> resultFile = new ArrayList<File>();
		if (!file.isDirectory()) {
			//throw new IllegalArgumentException(file + "不是一个有效的目录");
			return resultFile;
		}
		if (suffixs == null) {
			throw new IllegalArgumentException("请指定文件后缀");
		}
		File[] files = file.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile()) {
				for (int j = 0; j < suffixs.length; j++) {
					if (f.getPath().endsWith(suffixs[j])) {
						resultFile.add(f);
						break;
					}
				}
			}
		}
		return resultFile;
	}
	public static List<File> getFilesByFolder(String dir) {
		File file = new File(dir);
		List<File> resultFile = new ArrayList<File>();
		if (!file.isDirectory()) {
			//throw new IllegalArgumentException(file + "不是一个有效的目录");
			return resultFile;
		}
		
		File[] files = file.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isFile()) {
				resultFile.add(f);
			}
		}
		return resultFile;
	}
	public static List<File> getFoldersByFolder(String dir) {
		File file = new File(dir);
		List<File> resultFile = new ArrayList<File>();
		if (!file.isDirectory()) {
			//throw new IllegalArgumentException(file + "不是一个有效的目录");
			return resultFile;
		}
		
		File[] files = file.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			File f = files[i];
			if (f.isDirectory()) {
				resultFile.add(f);
			}
		}
		return resultFile;
	}
	public static String getUTF8Str(String str){
		if(!isValidString(str)){
			return "";
		}
		try {
			return new String(str.getBytes("iso-8859-1"),"utf-8");
		} catch (Exception e) {
			throw new RuntimeException("不支持的字符集?",e);
		}
	}
	public static String getCurrencyDateStr(){
		Date now=new Date();
		return formatter.format(now);
	}
	/**
	 * 返回的格式为 yyyy-MM-dd HH:mm:ss
	 * @param time
	 * @return
	 */
	public static String getDateTime(long time){
		Date date=new Date(time);
		return dateTimeFormatter.format(date);
	}
	/**
	 * 输入的格式为 yyyy-MM-dd HH:mm:ss
	 * @param dateStr
	 * @return
	 */
	public static Date getDateTime(String dateStr){
		try {
			return dateTimeFormatter.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException("转换时间异常!");
		}
	}
	/**
	 * 得到一段时间内所有的日期(字符串形式 yyyy-mm-dd)
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<String> getDateStrList(String startTime,String endTime){
		return getDateStrList(startTime, endTime, formatter);
	}
	/**
	 * 得到一段时间内所有的日期
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static List<String> getDateStrList(String startTime,String endTime,SimpleDateFormat formatter){
		List<String> dateStrList=new ArrayList<String>();
		
		Calendar cal1=Calendar.getInstance();
		java.util.Date date1=java.sql.Date.valueOf(startTime);
		java.util.Date date2=java.sql.Date.valueOf(endTime);
		if(!date1.before(date2)&&!date1.equals(date2)){
			throw new RuntimeException("结束时间必须大于开始时间!");
		}
		cal1.setTime(date1);
		long betweenDay=(date2.getTime()-date1.getTime())/1000/60/60/24;
		dateStrList.add(startTime);
		for(int i=0;i<betweenDay;i++){
			cal1.add(Calendar.DAY_OF_MONTH, 1);
			String dateStr=formatter.format(cal1.getTime());
			dateStrList.add(dateStr);
		}
		return dateStrList;
	}
	/**
	 * 返回的日期格式为yyyy-MM-dd
	 * @param time
	 * @return
	 */
	public static String getDateStr(long time){
		Date date=new Date(time);
		return formatter.format(date);
	}
	public static Connection getConn(String url,String user,String password){
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			throw new RuntimeException("获取数据库连接异常",e);
		}
	}
	public static List<Object> arrayToList(Object[] array){
		List<Object> list=new ArrayList<Object>();
		for(int i=0;i<array.length;i++){
			list.add(array[i]);
		}
		return list;
	}
	
	public static boolean isWindows(){
		return "\\".equals(File.separator);
	}
	public static Field[] getDeclaredFields(Class clazz){
		try {
			Field[] fields=clazz.getDeclaredFields();
			return fields;
		} catch (Exception e) {
			throw new RuntimeException("获取类的定义属性出现异常!");
		}
	}
	public static void setProperty(Object obj,String fieldName,Object value){
		try {
			Field field=obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			field.set(obj, value);
		} catch (Exception e) {
			throw new RuntimeException("通过反射给对象赋值失败",e);
		} 
	}
	public static Object invokeMethod(Object obj,String methodName,Class[] parameterTypes,Object[] args){
		try {
			Method method=obj.getClass().getDeclaredMethod(methodName, parameterTypes);
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw new RuntimeException("通过反射给调用方法失败",e);
		}
	}
	public static Object invokeMethod(Method method,Object obj,Object[] args){
		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw new RuntimeException("通过反射给调用方法失败",e);
		} 
	}
	public static Object getProperty(Object obj,String fieldName){
		try {
			Class clazz=obj.getClass();
			Field field=clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(obj);
		} catch (Exception e) {
			throw new RuntimeException("通过反射给获取属性值失败",e);
		}
	}
}
