/**
 * 
 */
package org.nutz.dao.cache.method;

import org.nutz.dao.cache.ObsArgClass;
import org.nutz.dao.convent.utils.CommonUtils;

/**
 * delete相关方法的缓存处理器
 * @author liaohongliu
 *
 * 创建时间: 2011-4-21
 */
public class DeleteMethodHandler implements IDaoCacheMethodHandler {

	/* (non-Javadoc)
	 * @see org.nutz.dao.cache.method.IDaoCacheMethodHandler#handler(org.nutz.dao.cache.ObsArgClass)
	 */
	public Object handler(ObsArgClass msg) {
		Object returnValue=CommonUtils.invokeMethod(msg.getMethod(), msg.getCacheStrategy().getDao(), msg.getArgs());
		Object[] args=msg.getArgs();
		if(args.length==1){//int delete(Object obj)
			Object key=msg.getCacheStrategy().getKey(args[0]);
			msg.getCache().remove(key);
		}
		if(args.length==2){
			//int delete(Class<?> classOfT, long id)
			//int delete(Class<?> classOfT, String name)
			if(args[0].getClass()==Class.class){
				Object key=msg.getCacheStrategy().getKey(args[0].getClass().getName());
				msg.getCache().remove(key);
			}else{
				//<T> void deleteWith(T obj, String regex)
				//<T> void deleteLinks(T obj, String regex)
				Object key=msg.getCacheStrategy().getKey(args[0]);
				msg.getCache().remove(key);
			}
		}
		if(args.length>2){//<T> int deletex(Class<T> classOfT, Object... pks)
			Object key=msg.getCacheStrategy().getKey(args[0].getClass().getName());
			msg.getCache().remove(key);
		}
		return returnValue;
	}

}
