package org.nutz.plugins.hbm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.nutz.dao.DaoException;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.impl.entity.NutEntity;
import org.nutz.dao.impl.entity.field.NutMappingField;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.Xmls;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 有限支持Hibernate的映射文件转为nutz实体映射
 * @author wendal(wendal1985@gmail.com)
 *
 */
@SuppressWarnings("rawtypes")
public class HbmEntityMaker implements EntityMaker {

	Map<Class, Entity> entites = new HashMap<Class, Entity>();

	@SuppressWarnings("unchecked")
	public <T> Entity<T> make(Class<T> type) {
		return entites.get(type);
	}

	public HbmEntityMaker() {}
	
	public HbmEntityMaker(String...paths) {
		setPaths(paths);
	}
	
	public void setPaths(String...paths) {
		for (String path : paths) {
			for (NutResource re : Scans.me().scan(path, ".hbm$")) {
				try {
					log.debug("add hbm : " + re.getName());
					addHbmStream(re.getInputStream());
				}
				catch (Exception e) {
					throw Lang.wrapThrow(e);
				}
			}
		}
	}
	
	public void addHbmStream(InputStream in) throws Exception {
		Document doc = Xmls.xmls().parse(in);
		Element root = doc.getDocumentElement();
		if (!"hibernate-mapping".equals(root.getNodeName())) {
			log.info("not root as hibernate-mapping, skip");
			return;
		}
		for (Element ele : Xmls.children(root, "^class$")) {
			_addMappingClass(ele);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void _addMappingClass(Element klassElement) throws Exception {
		String className = klassElement.getAttribute("name");
		String tableName = klassElement.getAttribute("table");
		if (Strings.isBlank(className)) {
			throw new DaoException("Blank class Name!");
		}
		if (Strings.isBlank(tableName)) {
			throw new DaoException("Blank table Name!");
		}
		NutEntity en = new NutEntity(Class.forName(className));
		en.setTableName(tableName);
		en.setViewName(tableName);
		Mirror mirror = Mirror.me(en.getType());
		
		Element pkElement = Xmls.firstChild(klassElement, "^id$");
		if (pkElement != null) {
			NutMappingField pk = ele2FieldMapping(en, pkElement);
			if (Mirror.me(mirror.getField(pk.getName()).getType()).isNumber()) {
				pk.setAsId();
			} else {
				pk.setAsName();
			}
			en.addMappingField(pk);
		}
		for (Element prop : Xmls.children(klassElement, "^property$")) {
			en.addMappingField(ele2FieldMapping(en, prop));
		}
		entites.put(en.getType(), en);
	}
	
	public NutMappingField ele2FieldMapping(NutEntity en, Element ele) {
		NutMappingField mappingField = new NutMappingField(en);
		String name = ele.getAttribute("name");
		if (Strings.isBlank(name)) {
			throw new DaoException("blank name property " + en.getType());
		}
		mappingField.setName(name);
		if (ele.hasAttribute("column")) {
			mappingField.setColumnName(ele.getAttribute("column"));
		} else {
			mappingField.setColumnName(name);
		}
		
		if (ele.hasAttribute("type")) {
			// TODO 支持type
		}
		
		if (ele.hasAttribute("length")) {
			mappingField.setWidth(Integer.parseInt(ele.getAttribute("length")));
		}
		
		return mappingField;
	}
	
	private static final Log log = Logs.get();
}
