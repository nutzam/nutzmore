package org.nutz.dao.convent.utils;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.nutz.dao.convent.tools.pojo.SqlXmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class AnalyzeSqlXml {
	private static Map<String, SqlXmlObject> sqlMap=new LinkedHashMap<String, SqlXmlObject>();
	private static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	private static DocumentBuilder builder;
	public static final String BASESQLFILE="sql.xml";
	private static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	static{
		initSql();
	}
	public static void initSql(){
		try {
			sqlMap=new LinkedHashMap<String, SqlXmlObject>();
			builder = factory.newDocumentBuilder();
			builder.setEntityResolver(new EntityResolver() {
				public InputSource resolveEntity(String publicId,String systemId) throws SAXException, IOException {
					return new InputSource(new StringReader(""));
				}
			});
			URL url=classLoader.getResource(BASESQLFILE);
			Document doc=builder.parse(url.toString());
			getSqlXmlObjects(doc);
			// 将include中的也加进来
			Document[] docs=getOtherSqlDocs(doc);
			for(int i=0;i<docs.length;i++){
				if(docs[i].getElementsByTagName("sqlMap").getLength()!=0){
					getSqlXmlObjectsForIbatis(docs[i]);
				}else{
					getSqlXmlObjects(docs[i]);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("解析sql文件出现异常!",e);
		}
	}
	public static void getSqlXmlObjectsForIbatis(Document doc){
		NodeList nodeList=doc.getElementsByTagName("sql");//得到所有标签为sql的node
		buildeXmlObject(nodeList);
		nodeList=doc.getElementsByTagName("select");//得到所有标签为select的node
		buildeXmlObject(nodeList);
		nodeList=doc.getElementsByTagName("insert");//得到所有标签为insert的node
		buildeXmlObject(nodeList);
		nodeList=doc.getElementsByTagName("update");//得到所有标签为update的node
		buildeXmlObject(nodeList);
	}
	private static void buildeXmlObject(NodeList nodeList) {
		for(int i=0;i<nodeList.getLength();i++){//遍历所有sqlnode
			SqlXmlObject sqlObject = new SqlXmlObject();//创建一个对象
			Node node=nodeList.item(i);//得到其中的一个
			NamedNodeMap nodeMap=node.getAttributes();
			String sqlId=nodeMap.getNamedItem("id").getNodeValue();//得到id属性
			
			NodeList list=node.getChildNodes();//得到sql node的子节点
			String sqlContent=list.item(0).getNodeValue();//得到其中的sql
			if(sqlContent==null||"".equals(sqlContent.trim())){
				if(list.getLength()>0){
					sqlContent=list.item(1).getNodeValue();
				}
				if(sqlContent==null||"".equals(sqlContent.trim())){
					throw new RuntimeException("在sqlId为"+sqlId+"中没有找到有效的sql语句!");
				}
			}
			String[] sqls=sqlContent.trim().split("\n");//按换行进行分割
			StringBuffer newSql=new StringBuffer();//定义一个新的字符串来存储修改后的sql语句
			for(int j=0;j<sqls.length;j++){
				newSql.append(fiterSql(sqls[j]));
			}
			sqlObject.setSqlId(sqlId);
			sqlObject.setSqlContent(newSql.toString().trim());
			//先判断是否已经有了,如果有就抛出异常
			Object sqlObj=sqlMap.get(sqlId);
			if(sqlObj!=null){
				throw new RuntimeException("已经存在编号为"+sqlId+"的sql语句!");
			}
			sqlMap.put(sqlId, sqlObject);//添加到map中
		}
	}
	public static Map getSqlXmlObjects(Document doc){
		NodeList nodeList=doc.getElementsByTagName("sql");//得到所有标签为sql的node
		buildeXmlObject(nodeList);
		return sqlMap;
	}
	private static Document[] getOtherSqlDocs(Document doc){
		NodeList nodeList=doc.getElementsByTagName("include");//得到所有标签为sql的node
		//Document[] docs=new Document[nodeList.getLength()];
		List<Document> docs=new ArrayList<Document>();
		for(int i=0;i<nodeList.getLength();i++){
			Node node=nodeList.item(i);//得到其中的一个
			NamedNodeMap nodeMap=node.getAttributes();
			String filePath=nodeMap.getNamedItem("file").getNodeValue();//得到file属性
			try {
				builder=factory.newDocumentBuilder();
				builder.setEntityResolver(new EntityResolver() {
					public InputSource resolveEntity(String publicId,String systemId) throws SAXException, IOException {
						return new InputSource(new StringReader(""));
					}
				});
				if(filePath.indexOf("*")==-1){
					URL url=classLoader.getResource(filePath);
					docs.add(builder.parse(url.toString()));
				}else{
					String baseFilePath=classLoader.getResource("").getPath();
					
					if(filePath.indexOf("/")!=-1){
						baseFilePath=baseFilePath+filePath.substring(0,filePath.lastIndexOf("/"));
					}
					baseFilePath=java.net.URLDecoder.decode(baseFilePath, "utf-8");
					File baseFile=new File(baseFilePath);
					if(!baseFile.isDirectory()){
						throw new IllegalArgumentException(baseFilePath+"不是一个有效的目录!");
					}
					String fileLikeName=filePath.substring(filePath.lastIndexOf("/")+1);
					
					File[] files=baseFile.listFiles();
					for(int j=0;j<files.length;j++){
						if(BASESQLFILE.equals(files[j].getName())){
							continue;
						}
						if(files[j].getName().indexOf(fileLikeName.replaceAll("\\*", ""))!=-1){
							docs.add(builder.parse(files[j]));
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("在解析文件:"+filePath+"出现异常",e);
			}
		}
		Document[] documents=new Document[docs.size()];
		return  docs.toArray(documents);
	}
	/**
	 * 过滤sql语句,主要是去掉注释
	 * @param sql
	 * @return
	 */
	public static String fiterSql(String sql){
		String newSql=sql;
		if(sql.indexOf("--")!=-1){
			newSql=sql.substring(0,sql.indexOf("--"));
		}
		if(newSql.indexOf("/*")!=-1){
			newSql=newSql.substring(0,newSql.indexOf("/*"));
		}
		return newSql;
	}
	public static Map getSqlMap(){
		return sqlMap;
	}
	/**
	 * 这个方法获得的sql语句会将ibatis的#name#样式改为?
	 * @param sqlId sql语句的编号
	 * @return sql语句
	 */
	public static String getSql(String sqlId){
		SqlXmlObject sqlObj= (SqlXmlObject) sqlMap.get(sqlId);
		if(sqlObj==null){
			throw new RuntimeException("没有找到sql编号为:"+sqlId+"的sql语句");
		}
		return sqlObj.getSqlContent().replaceAll("#[a-zA-Z]+[1-9]?#", "?");
	}
	/**
	 * 获得原始的sql语句
	 * @param sqlId sql语句的编号
	 * @return sql语句
	 */
	public static String getFormerSql(String sqlId){
		SqlXmlObject sqlObj= (SqlXmlObject) sqlMap.get(sqlId);
		if(sqlObj==null){
			throw new RuntimeException("没有找到sql编号为:"+sqlId+"的sql语句");
		}
		return sqlObj.getSqlContent();
	}
	public static void main(String[] args) {
		String sql=AnalyzeSqlXml.getFormerSql("saveUser");
		//AnalyzeSqlXml.initSql();
		//sql="select * from user/**/";
		System.out.println(sql);
		System.out.println(fiterSql(sql));
		System.out.println("insert into user(user_name,age,gender,birthday,update_dt) values(#userName#,#age#,#gender#,#birthday#,now())".replaceAll("#[a-zA-Z]+[1-9]?#", "?"));
		
	}
	
}
