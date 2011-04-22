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
	/**
	 * 根据对象获取缓存中的key
	 * @param <T> 
	 * @param obj 对象,必须要设置@Id,@name,@PK中至少一个
	 * @return 生成的主键,现在采用是字符串的主键
	 */
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
