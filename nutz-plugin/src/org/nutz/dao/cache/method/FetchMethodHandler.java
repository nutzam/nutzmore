/**
 * 
 */
package org.nutz.dao.cache.method;

import org.nutz.dao.cache.ObsArgClass;
import org.nutz.dao.convent.utils.CommonUtils;

/**
 * @author liaohongliu
 *
 * 创建时间: 2011-4-21
 */
public class FetchMethodHandler implements IDaoCacheMethodHandler{

	public Object handler(ObsArgClass msg) {
		Object[] args=msg.getArgs();
		if(args.length==1){
			//<T> T fetch(Class<T> classOfT)
			if(args[0] instanceof Class){
				Object key=msg.getCacheStrategy().getKey(((Class)args[0]).getName());
				return cacheHandle(msg, key);
			}else{
				//<T> T fetch(T obj)
				Object key=msg.getCacheStrategy().getKey(args[0]);
				return cacheHandle(msg, key);
			}
		}
		if(args.length==2){
			if(args[0] instanceof Class){
				String clazzName=((Class)args[0]).getName();
//				<T> T fetch(Class<T> classOfT, long id)
				if(args[1] instanceof Long){
					Object key=msg.getCacheStrategy().getKey(clazzName, (Long)args[1]);
					return this.cacheHandle(msg, key);
				}else if(args[1] instanceof String){
//				<T> T fetch(Class<T> classOfT, String name)
					Object key=msg.getCacheStrategy().getKey(clazzName, (String)args[1]);
					return this.cacheHandle(msg, key);
				}else{
					return CommonUtils.invokeMethod(msg.getMethod(), msg.getCacheStrategy().getDao(), msg.getArgs());
				}
			}else if(args[0] instanceof String){
				//Record fetch(String tableName, Condition condition)
				return CommonUtils.invokeMethod(msg.getMethod(), msg.getCacheStrategy().getDao(), msg.getArgs());
			}else{
				//<T> T fetchLinks(T obj, String regex)
				Object key=msg.getCacheStrategy().getKey(args[0]);
				return this.cacheHandle(msg, key);
			}
		}
		if(args.length>2){
			//<T> T fetchx(Class<T> classOfT, Object... pks)
			Object[] pks=new Object[args.length-1];
			System.arraycopy(args, 1, pks, 0, pks.length);
			Object key=msg.getCacheStrategy().getKey((Class)args[0], pks);
			return this.cacheHandle(msg, key);
		}
		return CommonUtils.invokeMethod(msg.getMethod(), msg.getCacheStrategy().getDao(), msg.getArgs());
	}

	private Object cacheHandle(ObsArgClass msg, Object key) {
		Object value=msg.getCache().get(key);
		if(value==null){
			value=CommonUtils.invokeMethod(msg.getMethod(), msg.getCacheStrategy().getDao(), msg.getArgs());
			if(value!=null){
				msg.getCache().put(key, value);
			}
		}
		return value;
	}
}
