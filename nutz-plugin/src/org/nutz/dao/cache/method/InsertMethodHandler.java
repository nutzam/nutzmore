/**
 * 
 */
package org.nutz.dao.cache.method;

import org.nutz.dao.Chain;
import org.nutz.dao.cache.ObsArgClass;
import org.nutz.dao.convent.utils.CommonUtils;

/**
 * insert相关方法的缓存处理器
 * @author liaohongliu
 *
 * 创建时间: 2011-4-21
 */
public class InsertMethodHandler implements IDaoCacheMethodHandler {

	/* (non-Javadoc)
	 * @see org.nutz.dao.cache.method.IDaoCacheMethodHandler#handler(org.nutz.dao.cache.ObsArgClass)
	 */
	public Object handler(ObsArgClass msg) {
		Object returnValue=CommonUtils.invokeMethod(msg.getMethod(), msg.getCacheStrategy().getDao(), msg.getArgs());
		Object[] args=msg.getArgs();
		if(args.length==1){
			//<T> T insert(T obj)
			//<T> T fastInsert(T obj)
			Object key=msg.getCacheStrategy().getKey(args[0]);
			msg.getCache().put(key,returnValue);
		}
		if(args.length==2){
			//void insert(Class<?> classOfT, Chain chain)
			if(args[0].getClass()==Class.class){
				Object key=msg.getCacheStrategy().getKey(((Class)args[0]).getName());
				msg.getCache().put(key,returnValue);
			}else if(args[0].getClass()==String.class){
				//void insert(String tableName, Chain chain)
				String className=msg.getCacheStrategy().getClassNameByTableName((String)args[0]);
				Object key=msg.getCacheStrategy().getKey(className);
				msg.getCache().put(key,returnValue);
			}else{
				//<T> T insertWith(T obj, String regex)
				//<T> T insertLinks(T obj, String regex)
				//<T> T insertRelation(T obj, String regex)
				Object key=msg.getCacheStrategy().getKey(args[0]);
				msg.getCache().put(key, returnValue);
			}
		}
		return returnValue;
	}
}
