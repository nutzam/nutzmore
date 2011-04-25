/**
 * 
 */
package org.nutz.dao.cache.method;

import java.util.Map;
import java.util.Set;

import org.nutz.dao.Chain;
import org.nutz.dao.Condition;
import org.nutz.dao.cache.Cache;
import org.nutz.dao.cache.ObsArgClass;
import org.nutz.dao.convent.utils.CommonUtils;

/**
 * update相关方法的缓存处理器
 * @author liaohongliu
 *
 * 创建时间: 2011-4-21
 */
public class UpdateMethodHandler implements IDaoCacheMethodHandler {

	/* (non-Javadoc)
	 * @see org.nutz.dao.cache.method.IDaoCacheMethodHandler#handler(org.nutz.dao.cache.ObsArgClass)
	 */
	public Object handler(ObsArgClass msg) {
		Object[] args=msg.getArgs();
		Object returnValue=CommonUtils.invokeMethod(msg.getMethod(), msg.getCacheStrategy().getDao(), args);
		//只有一个参数的情况,也就是根据主键更新的情况
		//或者updateWith,updateLinks
		//或者updateRelation
		if(args.length==1||args.length==2||args.length==4){
			updateCacheHandle(msg, args);
		}
		if(args.length==3){
			//int update(Class<?> classOfT, Chain chain, Condition condition);
			if(args[0] instanceof Class){
				this.updateCacheHandle(msg, args);
			}else if(args[0].getClass()==String.class){
				//int update(String tableName, Chain chain, Condition condition);
				//清除该表对应的所有对象
				String tableName=(String)args[0];
				removeRelationObj(msg, tableName);
			}
		}
		return returnValue;
	}
	/**
	 * 移除与指定表相关的缓存对象
	 * @param msg 封装的消息对象
	 * @param tableName 关联的表
	 */
	public static void removeRelationObj(ObsArgClass msg, String tableName) {
		String className=msg.getCacheStrategy().getClassNameByTableName(tableName);
		 removeRelationObjByClassName(msg, className);
	}
	public static void removeRelationObjByClassName(ObsArgClass msg, String className) {
		Cache cache=msg.getCache();
		Map<Object, Object> cacheMap=cache.toMap();
		Set<Object> keySet=cacheMap.keySet();
		for (Object key : keySet) {//这里暂时假设key是字符串
			if(key.toString().startsWith(className)){
				cache.remove(key);
			}
		}
	}
	private void updateCacheHandle(ObsArgClass msg, Object[] args) {
		Object[] keys=msg.getCacheStrategy().getAllKeys(args[0]);
		for (Object key : keys) {
			if(key!=null){
				msg.getCache().update(key, args[0]);
			}
		}
	}

}
