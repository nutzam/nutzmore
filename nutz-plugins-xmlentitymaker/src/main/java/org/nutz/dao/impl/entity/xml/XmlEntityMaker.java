package org.nutz.dao.impl.entity.xml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.nutz.dao.DB;
import org.nutz.dao.DaoException;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.entity.MacroType;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.entity.annotation.ColType;
import org.nutz.dao.impl.EntityHolder;
import org.nutz.dao.impl.entity.FieldMacroInfo;
import org.nutz.dao.impl.entity.NutEntity;
import org.nutz.dao.impl.entity.NutEntityIndex;
import org.nutz.dao.impl.entity.field.ManyLinkField;
import org.nutz.dao.impl.entity.field.ManyManyLinkField;
import org.nutz.dao.impl.entity.field.NutMappingField;
import org.nutz.dao.impl.entity.field.OneLinkField;
import org.nutz.dao.impl.entity.info.LinkInfo;
import org.nutz.dao.impl.entity.macro.ElFieldMacro;
import org.nutz.dao.impl.entity.macro.SqlFieldMacro;
import org.nutz.dao.jdbc.JdbcExpert;
import org.nutz.dao.jdbc.Jdbcs;
import org.nutz.dao.sql.Pojo;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.Xmls;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.resource.NutResource;
import org.nutz.resource.Scans;
import org.nutz.trans.Trans;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import static org.nutz.dao.impl.entity.xml.XmlEntityUtil.*;

/**
 * 基于XML配置Entity
 * @author wendal(wendal1985@gmail.com)
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class XmlEntityMaker implements EntityMaker {
	
	private static final Log log = Logs.get();
	
	Map<String, Entity> _map = new ConcurrentHashMap<String, Entity>();
	
	DocumentBuilder builder;
	
    private DataSource datasource;

    private JdbcExpert expert;

    private EntityHolder holder;
    
    protected Map<String, List<Element>> pendingLinks = new ConcurrentHashMap<String, List<Element>>();
    
    protected String[] paths;
    
    public XmlEntityMaker() throws ParserConfigurationException {
		builder = Lang.xmls();
	}
    
    public void init(DataSource datasource, JdbcExpert expert, EntityHolder holder) {
        this.datasource = datasource;
        this.expert = expert;
        this.holder = holder;
        if (this.paths != null) {
            try {
                setPaths(paths);
            }
            catch (Exception e) {
                throw new DaoException(e);
            }
            this.paths = null;
        }
    }
	
	public XmlEntityMaker(DataSource datasource, JdbcExpert expert, EntityHolder holder) throws ParserConfigurationException {
		this();
		init(datasource, expert, holder);
	}

	public <T> Entity<T> make(Class<T> type) {
		if (!_map.containsKey(type.getName()))
			// 并没有加载过指定的类型的实体
			throw new DaoException("#nutz-dao-xml-001# not such xml entity for type="+type);
		return _map.get(type.getName()); // 注意, 这里是单纯从缓存中获取已有的Entity, 因为是基于XML的
	}
	
	protected <T> Entity<T> getEntity(Class<T> klass) {
		if (holder.hasType(klass))
			return holder.getEntity(klass);
		return _map.get(klass.getName());
	}
	
	protected void setEntity(Entity<?> en) {
		_map.put(en.getType().getName(), en);
		holder.set(en);
	}
	
	public void setPaths(String ... paths) throws IOException, SAXException {
	    if (this.holder == null) {
	        this.paths = paths;
	        return;
	    }
		for (String path : paths) {
			addPath(path);
		}
		try {
			verify();
		} catch (DaoException e) {
			log.info("some relation pending are not complete", e);
		}
	}
	
	public void addPath(String path) throws IOException, SAXException {
		List<NutResource> files = Scans.me().scan(path, ".xml$");
		if (files.isEmpty())
			return;
		for (NutResource resource : files) {
			log.debug("load entity xml --> " + resource.getName());
			add(resource.getInputStream());
		}
	}
	
	public void add(InputStream ins) throws IOException, SAXException {
		add(builder.parse(ins));
	}
	
	public void add(Document document) {
		document.normalizeDocument();
		Element top = document.getDocumentElement();
		if (!top.getNodeName().endsWith(E_NUTZ_MAPPING)) { // 没有nutz-mapping, 自然没啥好说的了
			log.info("skip xml without nutz-mapping");
			return;
		}
		String topPackageName = top.getAttribute(A_PACKAGE);
		for (Element ele : Xmls.children(top, E_ENTITY)) { // 一个XML中允许包含多个entity描述
			addXmlEntity(ele, topPackageName);
		}
	}
	
	public void addXmlEntity(final Element ele, String topPackageName) {
		String type = ele.getAttribute(A_TYPE);
		if (Strings.isBlank(type)) // xsd有约束,这里再检查一次
			throw new DaoException("entity without type!!");
		Class<?> klass = null;
		try {
			klass = loadClass(topPackageName, type); // 尝试载入对应的类
		} catch (ClassNotFoundException e) {
			// 指定的类不存在
			throw new DaoException("#nutz-dao-xml-002# <entity> type ClassNotFound : " + type, e);
		}
		NutEntity<?> en = new NutEntity(klass);
		String tableName = null;
        if (Strings.isBlank(ele.getAttribute(A_TABLE))) {
        	tableName = Strings.lowerWord(klass.getSimpleName(), '_');
        	log.warnf("No @Table found, fallback to use table name='%s' for type '%s'", tableName, klass.getName());
        } else {
        	tableName = ele.getAttribute(A_TABLE);
        }
        String viewName = Strings.isBlank(ele.getAttribute(A_VIEW)) ? tableName : ele.getAttribute(A_VIEW);
        en.setTableName(tableName);
        en.setViewName(viewName);
		
        // 表注解
		String tableComment = ele.getAttribute(A_COMMENT);
		if (!Strings.isBlank(tableComment))
			en.setTableComment(tableComment);
		
		// TODO 支持default, readonly 注解所对应的xml配置
		
		// 先塞进去, 主要是为了避免1对1的自我映射
		setEntity(en);
		
		// 下面开始处理字段,索引,等等
		for (Element e : Xmls.children(ele)) {
			String ename = e.getNodeName();
			if (E_ID.equals(ename)) {
				addField(en, e, true, false);
			} else if (E_NAME.equals(ename)) {
				addField(en, e, false, true);
			} else if (E_FIELD.equals(ename)) {
				addField(en, e, false, false);
			} else if (E_INDEX.equals(ename)) {
				String indexName = e.getAttribute(A_NAME);
				if (Strings.isBlank(indexName)) {
					// <index>必须有name
					throw new DaoException("#nutz-dao-xml-003# <index> must have a name, entity=" + type);
				}
				String indexFields = e.getAttribute(A_FIELDS);
				if (Strings.isBlank(indexFields)) {
					// <index> 必须有fields
					throw new DaoException("#nutz-dao-xml-004# <index> must have fields, entity=" + type);
				}
				boolean indexUnique = e.getAttribute(A_UNIQUE).isEmpty() || Boolean.parseBoolean(e.getAttribute(A_UNIQUE));
				NutEntityIndex eIndex = new NutEntityIndex();
				eIndex.setName(indexName);
				eIndex.setUnique(indexUnique);
				String[] names = Strings.splitIgnoreBlank(indexFields, ",");
				for (String fieldName : names) {
					MappingField field = en.getField(fieldName);
					if (field == null)
						// <index>引用的属性不存在
						throw new DaoException("#nutz-dao-xml-005# <index> refer not-exist field, entity=" + type + ", field=" + fieldName);
					eIndex.addField(field);
				}
				en.addIndex(eIndex);
			} else if (E_ONE.equals(ename) || E_MANY.equals(ename) || E_MANYMANY.equals(ename)) {
				String target = e.getAttribute(A_TARGET);
				if (Strings.isBlank(target))
					// 必须指定target类型
					throw new DaoException("#nutz-dao-xml-006# target is emtry!!");
				Class<?> targetClass = null;;
				try {
					targetClass = loadClass(topPackageName, target);
				} catch (ClassNotFoundException e1) {
					// 关联类不存在!!
					throw new DaoException("#nutz-dao-xml-007# relation ClassNotFound entity=" + en.getType().getName() + ", relation class=" + target);
				}
				Entity relationEntity = getEntity(targetClass);
				if (relationEntity == null) {
					List<Element> list = pendingLinks.get(en.getType().getName());
					if (list == null) {
						list = new ArrayList<Element>();
						pendingLinks.put(en.getType().getName(), list);
						log.debug("add pending relation mapping " + e);
					}
					e.setAttribute("fullClassName", targetClass.getName());
					list.add(e);
				} else {
					addRelation(en, relationEntity, e);
				}
			}
		}
		
		// 处理一下复合主键
		String pks = ele.getAttribute(A_PKS);
		if (!Strings.isBlank(pks)) {
			String[] tmp = Strings.splitIgnoreBlank(pks, ",");
			for (String pk : tmp) {
				MappingField mf = en.getField(pk);
				if (mf != null)
					((NutMappingField)mf).setAsCompositePk();
			}
			en.checkCompositeFields(tmp);
		}
		
		if (null != datasource && null != expert) {
		    _checkupEntityFieldsWithDatabase(en);
		}
	}
	
	public void verify() throws DaoException {
		if (pendingLinks.isEmpty())
			return;
		for (Entry<String, List<Element>> pending : pendingLinks.entrySet()) {
			try {
				NutEntity en = (NutEntity) getEntity(loadClass(null, pending.getKey()));
				Iterator<Element> it = pending.getValue().iterator();
				while (it.hasNext()) {
					Element element = it.next();
					NutEntity relation = (NutEntity) getEntity(loadClass(null, element.getAttribute("fullClassName")));
					if (relation != null) {
						addRelation(en, relation, element);
						it.remove();
					}
				}
			} catch (ClassNotFoundException e) {
				throw Lang.impossible();
			}
			if (pending.getValue().isEmpty())
				pendingLinks.remove(pending.getKey());
		}
		if (!pendingLinks.isEmpty())
			// 校验失败, 存在未完成的关联关系.
			throw new DaoException("#nutz-dao-xml-008# xml entity verify, some relation can't link: " + pendingLinks.keySet());
	}
	
	protected void addRelation(NutEntity<?> en, Entity relationEntity, Element e) {
		LinkInfo info = new LinkInfo();
		info.name = e.getAttribute(A_NAME);
		try {
			info.fieldType = en.getMirror().getField(info.name).getType();
		} catch (NoSuchFieldException e1) {
			// 指定的属性并不存在!!!
			throw new DaoException("#nutz-dao-xml-009# field not exist. entity=" + en.getType() + ",field=" + info.name);
		}
		info.injecting = en.getMirror().getInjecting(info.name);
		info.ejecting = en.getMirror().getEjecting(info.name);

		String rname = e.getNodeName();
		if (E_ONE.equals(rname) || E_MANY.equals(rname)) {
			String keyName = e.getAttribute(A_KEY);
			MappingField mfKey = en.getField(keyName);
			if (keyName.isEmpty()) {
				log.infof("<%s> with emtry key, name=%s", rname, info.name);
			}
			else if(mfKey == null) {
				// 指定的key并不是主类的一个属性
				throw new DaoException(String.format("#nutz-dao-xml-010# %s <--> %s by field(%s) , but not exist", en.getType(), relationEntity.getType(), keyName));
			}
			String fieldName = e.getAttribute(A_FIELD);
			MappingField mfField = relationEntity.getField(fieldName);
			if (fieldName.isEmpty()) {
				log.infof("<%s> with emtry field, name=%s", rname, info.name);
			}
			else if (mfField == null) {
				// 指定的field并不是关联类的一个属性
				throw new DaoException(String.format("#nutz-dao-xml-011# %s <--> %s by field(%s) , but not exist", en.getType(), relationEntity.getType(), mfKey));
			}
			
			if (E_ONE.equals(rname)) {
				if (mfKey == null || mfField == null) {
					// one必须指定field和key
					throw new DaoException("#nutz-dao-xml-012# <one> must have key and field. name=" + info.name);
				}
				OneLinkField one = new OneLinkField(en, holder, info, relationEntity.getType(), mfKey, mfField);
				en.addLinkField(one);
			} else {
				ManyLinkField many = new ManyLinkField(en, holder, info, relationEntity.getType(), mfKey, mfField);
				en.addLinkField(many);
			}
		} else if (E_MANYMANY.equals(rname)) {
			// 多对多
			String from = e.getAttribute(A_FROM);
			String to = e.getAttribute(A_TO);
			String relation = e.getAttribute(A_RELATION);
			String key = e.getAttribute(A_KEY);
			ManyManyLinkField manymany = new ManyManyLinkField(en, holder, info, relationEntity.getType(), from, to, relation, key);
			en.addLinkField(manymany);
		}
	}

	protected void addField(NutEntity en, Element ele, boolean isId, boolean isName) {
		NutMappingField mf = new NutMappingField(en);
		// 处理name属性
		String name = ele.getAttribute(A_NAME);
		if (isId) {
			mf.setAsId();
			if (!"false".equals(ele.getAttribute(A_AUTO)))
				mf.setAsAutoIncreasement();
			if (Strings.isBlank(name))
				mf.setName("id");
			else
				mf.setName(name);
		} else if (isName) {
			mf.setAsName();
			if (Strings.isBlank(name))
				mf.setName("name");
			else
				mf.setName(name);
		} else {
			if (Strings.isBlank(name))
				// <field>必须有非空的name属性
				throw new DaoException("#nutz-dao-xml-013# <field> must have a name attribute, entity=" + en.getType().getName());
			mf.setName(name);
		}
		name = mf.getName();
		try {
			Field field = en.getMirror().getField(name);
			mf.setType(field.getType());
		} catch (NoSuchFieldException e) {
			// 类没有指定的属性!!
			throw new DaoException("#nutz-dao-xml-014# not such field in entity=" + en.getType().getName() + ", field=" + name);
		}

		// 处理column属性
		String columnName = ele.getAttribute(A_COLUMN);
		if (!Strings.isBlank(columnName))
			mf.setColumnName(columnName);
		else
			mf.setColumnName(name);
		
		// 处理comment属性
		if (!Strings.isBlank(ele.getAttribute(A_COMMENT))) {
			mf.setColumnComment(ele.getAttribute(A_COMMENT));
			en.setHasColumnComment(true);
		}
		
		// 处理一下coldefine, prev, next节点
		boolean flag = true;
		for (Element e : Xmls.children(ele)) {
			String ename = e.getNodeName();
			if (E_COLDEFINE.equalsIgnoreCase(ename)) {
				if (!e.getAttribute(A_CUSTOMTYPE).isEmpty() && !e.getAttribute(A_TYPE).isEmpty())
					flag = false;
				addColDefine(mf, e);
			} else if (E_PREV.equalsIgnoreCase(ename)) {
				_makeMacro(en, mf, e, true);
			} else if (E_NEXT.equals(ename)) {
				_makeMacro(en, mf, e, false);
			}
		}
		
		if (flag) {
			Jdbcs.guessEntityFieldColumnType(mf);
		}
		
		// 字段值的适配器
		if (expert != null)
			mf.setAdaptor(expert.getAdaptor(mf));
		
		mf.setInjecting(en.getMirror().getInjecting(name));
		mf.setEjecting(en.getMirror().getEjecting(name));
		
		en.addMappingField(mf);
	}
	
	protected void addColDefine(NutMappingField mf, Element ele) {
		if (hasAttr(ele, A_TYPE))
			mf.setColumnType(ColType.valueOf(ele.getAttribute(A_TYPE)));
		if (hasAttr(ele, A_WIDTH))
			mf.setWidth(Integer.parseInt(ele.getAttribute(A_WIDTH)));
		if (mf.getWidth() == 0 && mf.getColumnType() == ColType.VARCHAR)
			mf.setWidth(50);
		
		if (hasAttr(ele, A_PRECISION))
			mf.setPrecision(Integer.parseInt(ele.getAttribute(A_PRECISION)));
		if ("true".equals(ele.getAttribute(A_NOTNULL)))
			mf.setAsNotNull();
		if ("true".equals(ele.getAttribute(A_UNSIGNED)))
			mf.setAsUnsigned();
		// 无视auto设置
		// --------------
		if (hasAttr(ele, A_CUSTOMTYPE))
			mf.setCustomDbType(ele.getAttribute(A_CUSTOMTYPE));
		if ("false".equals(ele.getAttribute(A_INSERT)))
			mf.setInsert(false);
		if ("false".equals(ele.getAttribute(A_UPDATE)))
			mf.setUpdate(false);
	
	}
	
	protected boolean hasAttr(Element ele, String attrName) {
		return !Strings.isBlank(ele.getAttribute(attrName));
	}
	
	protected void _makeMacro(NutEntity en, NutMappingField mf, Element ele, boolean isPrev) {
		List<FieldMacroInfo> list = new ArrayList<FieldMacroInfo>();
		for(Element e : Xmls.children(ele)) {
			String nodeName = e.getNodeName();
			DB db = hasAttr(e, A_DB) ? DB.valueOf(e.getAttribute(A_DB)) : DB.OTHER;
			if (E_SQL.equals(nodeName)) {
				FieldMacroInfo sql = new FieldMacroInfo(MacroType.SQL, db, Xmls.getText(e));
				list.add(sql);
			} else if (E_EL.equals(nodeName)) {
				FieldMacroInfo el = new FieldMacroInfo(MacroType.EL, db, Xmls.getText(e));
				list.add(el);
			}
		};
		if (isPrev)
			en.addBeforeInsertMacro(__macro(mf, list));
		else
			en.addAfterInsertMacro(__macro(mf, list));
	}
	
    private void _checkupEntityFieldsWithDatabase(NutEntity<?> en) {
        Connection conn = null;
        try {
            conn = Trans.getConnectionAuto(datasource);
            expert.setupEntityField(conn, en);
        }
        catch (Exception e) {
            if (log.isDebugEnabled())
                log.debugf("Fail to setup '%s'(%s) by DB, because: (%s)'%s'",
                           en.getType().getName(),
                           en.getTableName(),
                           e.getClass().getName(),
                           e.getMessage());
        }
        finally {
            Trans.closeConnectionAuto(conn);
        }
    }
    
    private Pojo __macro(MappingField ef, List<FieldMacroInfo> infoList) {
        FieldMacroInfo theInfo = null;
        // 根据当前数据库，找到合适的宏
        for (FieldMacroInfo info : infoList) {
            if (DB.OTHER == info.getDb()) {
                theInfo = info;
            } else if (info.getDb().name().equalsIgnoreCase(expert.getDatabaseType())) {
                theInfo = info;
                break;
            }
        }
        // 如果找到，增加
        if (null != theInfo) {
            if (theInfo.isEl())
                return new ElFieldMacro(ef, theInfo.getValue());
            else
                return new SqlFieldMacro(ef, theInfo.getValue());
        }
        return null;
    }
    
    protected Class<?> loadClass(String topPackageName, String type) throws ClassNotFoundException {
    	if (Strings.isBlank(topPackageName))
    		return Class.forName(type);
    	return Class.forName(topPackageName + "." + type);
	}
}
