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
				String className=msg.getCacheStrategy().getClassNameByTableName((String)args[0]);
				Cache cache=msg.getCache();
				Map<Object, Object> cacheMap=cache.toMap();
				Set<Object> keySet=cacheMap.keySet();
				for (Object key : keySet) {//这里暂时假设key是字符串
					if(key.toString().startsWith(className)){
						cache.remove(key);
					}
				}
			}
		}
		return returnValue;
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
