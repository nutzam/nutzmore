/**
 * 
 */
package org.nutz.dao.entity.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.nutz.dao.Daos;
import org.nutz.dao.DatabaseMeta;
import org.nutz.dao.TableName;
import org.nutz.dao.convent.collect.data.CollectData;
import org.nutz.dao.convent.orm.DefaultOrmRule;
import org.nutz.dao.convent.orm.IOrmRule;
import org.nutz.dao.convent.tools.pojo.MyField;
import org.nutz.dao.convent.tools.pojo.MyTable;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.EntityMaker;
import org.nutz.dao.entity.EntityName;
import org.nutz.dao.entity.ErrorEntitySyntaxException;
import org.nutz.dao.entity.FieldType;
import org.nutz.dao.entity.Link;
import org.nutz.dao.entity.ValueAdapter;
import org.nutz.dao.entity.annotation.Column;
import org.nutz.dao.entity.annotation.Default;
import org.nutz.dao.entity.annotation.Id;
import org.nutz.dao.entity.annotation.Many;
import org.nutz.dao.entity.annotation.ManyMany;
import org.nutz.dao.entity.annotation.Name;
import org.nutz.dao.entity.annotation.Next;
import org.nutz.dao.entity.annotation.NotColumn;
import org.nutz.dao.entity.annotation.One;
import org.nutz.dao.entity.annotation.PK;
import org.nutz.dao.entity.annotation.Prev;
import org.nutz.dao.entity.annotation.Readonly;
import org.nutz.dao.entity.annotation.Table;
import org.nutz.dao.entity.annotation.View;
import org.nutz.dao.entity.born.Borns;
import org.nutz.dao.entity.next.FieldQuery;
import org.nutz.dao.entity.next.FieldQuerys;
import org.nutz.dao.sql.FieldAdapter;
import org.nutz.lang.Lang;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.segment.CharSegment;
import org.nutz.lang.segment.Segment;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * 基于约定大于配置的思想来实现orm,兼容注解的形式,
 * 也就是说如果有注解,则采用注解的方式mapping,如果没有就按约定的orm规则来实现
 * 默认的orm规则的是,数据库中字段去除下划线之后,首字母大写,比如user_name->userName
 * 如果要自己定义规则,请实现IOrmRule接口
 * @author liaohongliu
 *
 * 创建时间: 2011-2-14
 */
public class ConventionEntityMaker implements EntityMaker{

	private IOrmRule ormRule=new DefaultOrmRule();
	private static final Log log = Logs.getLog(ConventionEntityMaker.class);
	
	public Entity<?> make(DatabaseMeta db, Connection conn, Class<?> type) {
		//先判断是否有注解
		String tableName=null;
		Table tableAnnotation=type.getAnnotation(Table.class);
		if(tableAnnotation==null){//如果有table标签
			tableName=this.getOrmRule().class2TableName(type.getSimpleName());
		}else{
			tableName=tableAnnotation.value();
		}
		String viewName=null;
		View viewAnnotation=type.getAnnotation(View.class);
		if(viewAnnotation==null&&tableAnnotation==null){//如果有view标签
			viewName=this.getOrmRule().class2TableName(type.getSimpleName());
		}else if(tableAnnotation!=null){
			viewName=tableAnnotation.value();
		}else{
			viewName=viewAnnotation.value();
		}
//		得到表结构
		MyTable table=CollectData.getTableByTableName(tableName, conn,false);
		Entity<?> entity = new Entity<Object>();
		Mirror<?> mirror = Mirror.me(type);
		entity.setMirror(mirror);
		//设置表名
		entity.setTableName(EntityName.create(tableName));
		entity.setViewName(EntityName.create(viewName));
		//设置创建方式
		entity.setBorning(Borns.evalBorning(entity));
		//得到所有定义的字段
		Field[] fields=mirror.getFields();
		List<FieldQuery> befores = new ArrayList<FieldQuery>(5);
		List<FieldQuery> afters = new ArrayList<FieldQuery>(5);
		for (Field field : fields) {
			if(field.getAnnotation(NotColumn.class)!=null){
				continue;
			}
			Link link = evalLink(db, conn, mirror, field);
			if (null != link) {
				entity.addLinks(link);
			}else{
				EntityField ef = new EntityField(entity, field);
				//生成属性对象
				evalField(db, table, entity, field, ef);
				entity.addField(ef);
				if (null != ef.getBeforeInsert()){
					befores.add(ef.getBeforeInsert());
				}else if (null != ef.getAfterInsert()){
					afters.add(ef.getAfterInsert());
				}
			}
		}
		entity.setBefores(befores.toArray(new FieldQuery[befores.size()]));
		entity.setAfters(afters.toArray(new FieldQuery[afters.size()]));
		//联合主键处理
		evalPks(type, table, entity);
		
		return entity;
	}

	private void evalPks(Class<?> type, MyTable table, Entity<?> entity) {
		HashMap<String, EntityField> pkmap = new HashMap<String, EntityField>();
		PK pk = type.getAnnotation(PK.class);
		if (null != pk) {
			for (String pknm : pk.value()){
				EntityField ef=entity.getField(pknm);
				pkmap.put(pknm, ef);
			}
		}else{
			List<MyField> pkFields=table.getPkFields();
			if(pkFields.size()>=2){//判断是不是有多个主键,如果是的话就设置到PKmap中
				for (MyField field : pkFields) {
					String fieldName=this.getOrmRule().dbField2JavaField(field.getFieldName());
					EntityField ef=entity.getField(fieldName);
					pkmap.put(fieldName, ef);
				}
			}
		}
		Collection<EntityField> pks=pkmap.values();
		for (EntityField ef : pks) {
			ef.setType(FieldType.PK);
		}
		
	}
	
	private void evalField(DatabaseMeta db, MyTable table, Entity<?> entity, Field field, EntityField ef) {
		field.setAccessible(true);
		Column column = field.getAnnotation(Column.class);
		String columnName=null;
		
		if (null == column || Strings.isBlank(column.value())){
			columnName=this.getOrmRule().javaField2DbField(field.getName());
		}else{
			columnName=column.value();
		}
		ef.setColumnName(columnName);
		ef.setReadonly((field.getAnnotation(Readonly.class) != null));
		MyField myField=table.getField(columnName);
		ef.setNotNull(!myField.isAllowNull());
//		@Default
		Default dft = field.getAnnotation(Default.class);
		if (null != dft) {//如果定义了注解
			ef.setDefaultValue(new CharSegment(dft.value()));
		}else if(myField.getDefaultValue()!=null){//如果没有就尝试从数据库中获取
			ef.setDefaultValue(new CharSegment(myField.getDefaultValue().toString()));
		}
//		@Prev
		Prev prev = field.getAnnotation(Prev.class);
		if (null != prev) {
			ef.setBeforeInsert(FieldQuerys.eval(db, prev.value(), ef));
		}

		// @Next
		Next next = field.getAnnotation(Next.class);
		if (null != next) {
			ef.setAfterInsert(FieldQuerys.eval(db, next.value(), ef));
		}
//		@Id
		Id id = field.getAnnotation(Id.class);
		if (null != id) {
			// Check
			if (!ef.getMirror().isIntLike())
				throw error(entity, "@Id field [%s] must be a Integer!", field.getName());
			if (id.auto()) {
				ef.setType(FieldType.SERIAL);
				// 如果是自增字段，并且没有声明 '@Next' ，为其增加 SELECT MAX(id) ...
				if (null == field.getAnnotation(Next.class)) {
					ef.setAfterInsert(FieldQuerys.create("SELECT MAX($field) FROM $view", ef));
				}
			} else {
				ef.setType(FieldType.ID);
			}
		}

		// @Name
		Name name = field.getAnnotation(Name.class);
		if (null != name) {
			// Check
			if (!ef.getMirror().isStringLike())
				throw error(entity, "@Name field [%s] must be a String!", field.getName());
			// Not null
			ef.setNotNull(true);
			// Set Name
			if (name.casesensitive())
				ef.setType(FieldType.CASESENSITIVE_NAME);
			else
				ef.setType(FieldType.NAME);
		}

		// Prepare how to adapt the field value to PreparedStatement
		ef.setFieldAdapter(FieldAdapter.create(ef.getMirror(), ef.isEnumInt()));

		// Prepare how to adapt the field value from ResultSet
		ef.setValueAdapter(ValueAdapter.create(ef.getMirror(), ef.isEnumInt()));
		
	}
	
	private Link evalLink(DatabaseMeta db, Connection conn, Mirror<?> mirror, Field field) {
		try {
			// @One
			One one = field.getAnnotation(One.class);
			if (null != one) { // One > refer own field
				Mirror<?> ta = Mirror.me(one.target());
				Field referFld = mirror.getField(one.field());
				Field targetPkFld = lookupPkByReferField(ta, referFld);
				return Link.getLinkForOne(mirror, field, ta.getType(), referFld, targetPkFld);
			}
			Many many = field.getAnnotation(Many.class);
			if (null != many) {
				Mirror<?> ta = Mirror.me(many.target());
				Field pkFld;
				Field targetReferFld;
				if (Strings.isBlank(many.field())) {
					pkFld = null;
					targetReferFld = null;
				} else {
					targetReferFld = ta.getField(many.field());
					pkFld = lookupPkByReferField(mirror, targetReferFld);
				}

				return Link.getLinkForMany(	mirror,
											field,
											ta.getType(),
											targetReferFld,
											pkFld,
											many.key());
			}
			ManyMany mm = field.getAnnotation(ManyMany.class);
			if (null != mm) {
				// Read relation
				Statement stat = null;
				ResultSet rs = null;
				ResultSetMetaData rsmd = null;
				boolean fromName = false;
				boolean toName = false;
				try {
					stat = conn.createStatement();
					Segment tableName = new CharSegment(mm.relation());
					rs = stat.executeQuery(db.getResultSetMetaSql(TableName.render(tableName)));
					rsmd = rs.getMetaData();
					fromName = !Daos.isIntLikeColumn(rsmd, mm.from());
					toName = !Daos.isIntLikeColumn(rsmd, mm.to());
				}
				catch (Exception e) {
					if (log.isWarnEnabled())
						log.warnf("Fail to get table '%s', '%s' and '%s' "
									+ "will be taken as @Id ", mm.relation(), mm.from(), mm.to());
				}
				finally {
					Daos.safeClose(stat, rs);
				}
				Mirror<?> ta = Mirror.me(mm.target());
				Field selfPk = mirror.getField(fromName ? Name.class : Id.class);
				Field targetPk = ta.getField(toName ? Name.class : Id.class);
				return Link.getLinkForManyMany(	mirror,
												field,
												ta.getType(),
												selfPk,
												targetPk,
												mm.key(),
												mm.relation(),
												mm.from(),
												mm.to());
				// return Link.getLinkForManyMany(mirror, field, mm.target(),
				// mm.key(), mm.from(), mm
				// .to(), mm.relation(), fromName, toName);
			}
		}
		catch (NoSuchFieldException e) {
			throw Lang.makeThrow(	"Fail to eval linked field '%s' of class[%s] for the reason '%s'",
									field.getName(),
									mirror.getType().getName(),
									e.getMessage());
		}
		return null;
	}
	private static Field lookupPkByReferField(Mirror<?> mirror, Field fld)
			throws NoSuchFieldException {
		Mirror<?> fldType = Mirror.me(fld.getType());

		if (fldType.isStringLike()) {
			return mirror.getField(Name.class);
		} else if (fldType.isIntLike()) {
			return mirror.getField(Id.class);
		}
		throw Lang.makeThrow("'%s'.'%s' can only be CharSequence or Integer",
				fld.getDeclaringClass().getName(), fld.getName());
	}
	private ErrorEntitySyntaxException error(Entity<?> entity, String fmt, Object... args) {
		return new ErrorEntitySyntaxException(String.format("[%s] : %s",
															null == entity	? "NULL"
																			: entity.getType()
																					.getName(),
															String.format(fmt, args)));
	}
	public IOrmRule getOrmRule() {
		return ormRule;
	}
	public void setOrmRule(IOrmRule ormRule) {
		this.ormRule = ormRule;
	}
}
