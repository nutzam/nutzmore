/**
 * 
 */
package org.nutz.dao.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.nutz.dao.Condition;
import org.nutz.dao.Dao;
import org.nutz.dao.convent.utils.CommonUtils;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.EntityField;
import org.nutz.dao.entity.EntityHolder;

/**
 * 缓存策略类
 * @author liaohongliu
 *
 * 创建时间: 2011-4-21
 */
public class CacheStrategy{
	
	private Dao dao;
	
	public <T> Object getKey(T obj){
		Class<T> clazz=(Class<T>) obj.getClass();
		String clazzName=clazz.getName();
		Entity<T> entity = this.getDao().getEntity(clazz);
//		@Id型的
		Serializable id=entity.getId(obj);
		if((Long)id!=0){
			return this.getKey(clazzName, (Long)id);
		}
//		@name型的
		id=entity.getName(obj);
		if((String)id!=null){
			return this.getKey(clazzName, (String)id);
		}
//		@PK型
		return buildKeyForPK(obj, entity);
	}
	/**
	 * 这个方法主要是考虑到既有@Id,又有@name注解的类
	 * @param <T>
	 * @param obj
	 * @return
	 */
	public <T> Object[] getAllKeys(T obj){
		Class<T> clazz=(Class<T>) obj.getClass();
		String clazzName=clazz.getName();
		Entity<T> entity = this.getDao().getEntity(clazz);
		List<Object> keysList=new ArrayList<Object>();
//		@Id型的
		Serializable id=entity.getId(obj);
		if((Long)id!=0){
			keysList.add(this.getKey(clazzName, (Long)id));
		}
//		@name型的
		id=entity.getName(obj);
		if((String)id!=null){
			keysList.add(this.getKey(clazzName, (String)id));
		}
//		@PK型
		if(keysList.size()==0){//这里的假设和nutz框架的假设一样,也就是说如果设置了前两个注解是不会再设置这个注解的
			keysList.add(buildKeyForPK(obj, entity));
		}
		return keysList.toArray();
	}
	public Object getKey(Class itemClass,Object[] args){
		String clazzName=itemClass.getName();
		//联合主键
		if(args.length>2){
			return this.getKey(clazzName, args);
		}
		if(args.length==1){
			Class firstArgType=args[0].getClass();
			if(firstArgType==Class.class){//fetch class
				return this.getKey(firstArgType.getName());
			}else{//fetch obj
				return this.getKey(args[0]);
			}
		}
		Object key=args[1];
		Class secondArgType=key.getClass();
		if(secondArgType==Long.class){
			return this.getKey(clazzName, (Long)key);
		}
		if(secondArgType==String.class){
			return this.getKey(clazzName, (String)key);
		}
		if(secondArgType==Condition.class){//查询缓存,暂不支持
			return null;
		}
		throw new RuntimeException("不支持的缓存方法!请匹配合适的版本!");
	}
	public String getKey(String clazzName,long id){
		return clazzName+"#"+id;
	}
	public String getKey(String clazzName,String id){
		return clazzName+"#"+id;
	}
	public String getKey(String clazzName,Object...keyValues){
		String suffixKey="";
		for(int i=0;i<keyValues.length;i++){
			suffixKey=suffixKey+keyValues[i];
			if(i!=keyValues.length-1){
				suffixKey=suffixKey+",";
			}
		}
		return clazzName+"#"+suffixKey;
	}
	public String getKey(String clazzName){
		return clazzName+"#";
	}
	public String getClassNameByTableName(String tableName){
		EntityHolder entities=(EntityHolder) CommonUtils.getProperty(dao, "entities");
		Map<Class<?>, Entity<?>> mappings=(Map<Class<?>, Entity<?>>) CommonUtils.getProperty(entities, "mappings");
		Collection<Entity<?>> beanEntities=mappings.values();
		Entity beanEntity=null;
		for (Entity<?> entity : beanEntities) {
			if(entity.getTableName().equals(tableName)){
				beanEntity=entity;
				break;
			}
		}
		if(beanEntity!=null){
			return beanEntity.getType().getName();
		}
		return null;
	}
	private <T> String buildKeyForPK(T item, Entity<T> entity) {
		List<Object> keyValues=new ArrayList<Object>();
		EntityField[] keyFields=entity.getPkFields();
		for (EntityField field : keyFields) {
			Object value=field.getValue(item);
			keyValues.add(value);
		}
		return this.getKey(item.getClass().getName(), keyValues.toArray());
	}
	public Dao getDao() {
		if(this.dao==null){
			throw new RuntimeException("必须先设置dao才能使用!");
		}
		return dao;
	}
	public void setDao(Dao dao) {
		this.dao = dao;
	}
}
