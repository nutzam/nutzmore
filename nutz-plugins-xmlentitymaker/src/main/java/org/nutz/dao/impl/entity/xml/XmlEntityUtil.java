package org.nutz.dao.impl.entity.xml;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.EntityIndex;
import org.nutz.dao.entity.LinkField;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.PkType;
import org.nutz.dao.impl.entity.NutEntity;
import org.nutz.dao.impl.entity.field.ManyLinkField;
import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.dao.impl.entity.field.OneLinkField;
import org.nutz.dao.impl.entity.macro.SqlFieldMacro;
import org.nutz.dao.sql.Sql;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.Xmls;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class XmlEntityUtil {
	
	private static final Log log = Logs.get();
	
	public static final String E_NUTZ_MAPPING = "nutz-mapping";
	public static final String E_ENTITY = "entity";
	public static final String E_ID = "id";
	public static final String E_NAME = "name";
	public static final String E_FIELD = "field";
	public static final String E_PREV = "prev";
	public static final String E_NEXT = "next";
	public static final String E_SQL = "sql";
	public static final String E_EL = "el";
	public static final String E_INDEX = "index";
	public static final String E_COLDEFINE = "coldefine";
	
	public static final String A_PACKAGE = "package";
	public static final String A_TYPE = "type";
	public static final String A_TABLE = "table";
	public static final String A_VIEW = "view";
	public static final String A_COMMENT = "comment";
	public static final String A_NAME = "name";
	public static final String A_FIELDS = "fields";
	public static final String A_AUTO = "auto";
	public static final String A_UNIQUE = "unique";
	public static final String A_FIELD = "field";
	public static final String E_ONE = "one";
	public static final String E_MANY = "many";
	public static final String E_MANYMANY = "manymany";
	public static final String A_KEY = "key";
	public static final String A_RELATION = "relation";
	public static final String A_FROM = "from";
	public static final String A_TO = "to";
	public static final String A_COLUMN = "column";
	public static final String A_TARGET = "target";
	public static final String A_PKS = "pks";
	

	public static final String A_WIDTH = "width";
	public static final String A_PRECISION = "precision";
	public static final String A_NOTNULL = "notNull";
	public static final String A_UNSIGNED = "unsigned";
	public static final String A_CUSTOMTYPE = "customType";
	public static final String A_INSERT = "insert";
	public static final String A_UPDATE = "update";
	public static final String A_DB = "db";
	
	
	Document doc;
	
	Element top;
	
	public XmlEntityUtil() throws ParserConfigurationException  {
		doc = Xmls.xmls().newDocument();
		top = doc.createElement(E_NUTZ_MAPPING);
		doc.appendChild(top);
	}
	
	public void add(NutEntity en) {
		Element ee = doc.createElement(E_ENTITY);
		
		ee.setAttribute(A_TYPE, en.getType().getName());
		ee.setAttribute(A_TABLE, en.getTableName());
		ee.setAttribute(A_VIEW, en.getViewName());
		if (en.hasTableComment())
			ee.setAttribute(A_COMMENT, en.getTableComment());

		// 字段属性
		for (MappingField mf : (List<MappingField>)en.getMappingFields()) {
			ee.appendChild(addMappingField(mf, en));
		}
		
		// 复合主键
		if (en.getPkType() == PkType.COMPOSITE) {
			List<String> fieldNames = new ArrayList<String>();
			for(Object mf : en.getPks()) {
				fieldNames.add(((EntityField)mf).getName());
			}
			ee.setAttribute(A_PKS, Strings.join(",", fieldNames.toArray(new String[fieldNames.size()])));
		}
		
		// 关联
		for (LinkField lf : (List<LinkField>)en.getLinkFields(null)) {
			ee.appendChild(addLinkField(lf));
		}
		
		// 索引
		List<EntityIndex> indexs = en.getIndexes();
		for (EntityIndex index : indexs) {
			Element tmp = doc.createElement(E_INDEX);
			tmp.setAttribute(A_NAME, index.getName(en));
			List<String> fieldNames = new ArrayList<String>();
			for(EntityField mf : index.getFields()) {
				fieldNames.add(mf.getName());
			}
			tmp.setAttribute(A_FIELDS, Strings.join(",", fieldNames.toArray(new String[fieldNames.size()])));
			if (!index.isUnique())
				tmp.setAttribute(A_UNIQUE, ""+index.isUnique());
			ee.appendChild(tmp);
		}
		
		
		top.appendChild(ee);
	}

	protected Element addLinkField(LinkField lf) {
		if (lf instanceof OneLinkField) {
			Element ele = doc.createElement(E_ONE);
			ele.setAttribute(A_NAME, lf.getName());
			ele.setAttribute(A_TARGET, lf.getLinkedEntity().getType().getName());
			ele.setAttribute(A_FIELD, lf.getLinkedField().getName());
			ele.setAttribute(A_KEY, lf.getHostField().getName());
			return ele;
		} else if (lf instanceof ManyLinkField) {
			Element ele = doc.createElement(E_MANY);
			ele.setAttribute(A_NAME, lf.getName());
			ele.setAttribute(A_TARGET, lf.getLinkedEntity().getType().getName());
			if (lf.getHostField() == null) {
				log.warn("@Many for all record. name=" + lf.getName());
				ele.setAttribute(A_KEY, "");
				ele.setAttribute(A_FIELD, "");
			} else {
				ele.setAttribute(A_KEY, lf.getHostField().getName());
				ele.setAttribute(A_FIELD, lf.getLinkedField().getName());
			}
			return ele;
		} else if (lf instanceof ManyManyLinkField) {
			Element ele = doc.createElement(E_MANYMANY);
			ManyManyLinkField mmlf = (ManyManyLinkField)lf;
			ele.setAttribute(A_NAME, lf.getName());
			ele.setAttribute(A_TARGET, lf.getLinkedEntity().getType().getName());
			ele.setAttribute(A_RELATION, mmlf.getRelationName());
			ele.setAttribute(A_FROM, lf.getHostField().getName() + ":" + mmlf.getFromColumnName());
			ele.setAttribute(A_TO, lf.getLinkedField().getName() + ":" + mmlf.getToColumnName());
			//manymany.setAttribute(A_KEY, lf.getHostField().getName());
			return ele;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	protected Element addMappingField(MappingField mf, NutEntity en) {
		String ename = E_FIELD;
		if (mf.isId()) {
			ename = E_ID;
		} else if (mf.isName()) {
			ename = E_NAME;
		}
		Element field = doc.createElement(ename);
		if (mf.isId() && !mf.isAutoIncreasement()) {
			field.setAttribute(A_AUTO, "false");
		}
		if (!ename.equals(mf.getName()))
			field.setAttribute(A_NAME, mf.getName());
		if (mf.hasColumnComment()) {
			field.setAttribute(A_COMMENT, mf.getColumnComment());
		}
		if (!mf.getName().equals(mf.getColumnName()))
			field.setAttribute(A_COLUMN, mf.getColumnName());
		
		// TODO 完成prev和next
		Element prev = doc.createElement(E_PREV);
		Mirror<SqlFieldMacro> mirror = Mirror.me(SqlFieldMacro.class);
		List prevs = en.cloneBeforeInsertMacroes();
		for (Object object : prevs) {
			if (object instanceof SqlFieldMacro) {
				SqlFieldMacro sql = (SqlFieldMacro)object;
				MappingField smf = (MappingField) mirror.getValue(sql, "entityField");
				Sql _sql = (Sql) mirror.getValue(sql, "sql");
				if (smf.getName().equals(mf.getName())) {
					Element esql = doc.createElement(E_SQL);
					esql.setTextContent(_sql.getSourceSql());
					prev.appendChild(esql);
				}
			}
		}
		Element next = doc.createElement(E_NEXT);
		List nexts = en.cloneAfterInsertMacroes();
		for (Object object : nexts) {
			if (object instanceof SqlFieldMacro) {
				SqlFieldMacro sql = (SqlFieldMacro)object;
				MappingField smf = (MappingField) mirror.getValue(sql, "entityField");
				Sql _sql = (Sql) mirror.getValue(sql, "sql");
				if (smf.getName().equals(mf.getName())) {
					Element esql = doc.createElement(E_SQL);
					esql.setTextContent(_sql.getSourceSql());
					next.appendChild(esql);
				}
			}
		}
		if (prev.getChildNodes().getLength() != 0) {
			field.appendChild(prev);
		}
		if (next.getChildNodes().getLength() != 0) {
			field.appendChild(next);
		}
		// TODO 完成coldefine
		
		return field;
	}

	public Document doc() {
		return doc;
	}
	
	public String asXmlString() throws TransformerException {
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamResult sr = new StreamResult(out);
		transformer.transform(source, sr);
		return new String(out.toByteArray());
	}
	
	@SuppressWarnings("rawtypes")
	public static String asXML(NutEntity en) throws ParserConfigurationException, TransformerException {
		XmlEntityUtil u = new XmlEntityUtil();
		u.add(en);
		return u.asXmlString();
	}
}
