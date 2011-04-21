/**
 * 
 */
package org.nutz.dao.cache.method;

import java.lang.reflect.Method;

import org.nutz.dao.Dao;
import org.nutz.dao.cache.Cache;
import org.nutz.dao.cache.CacheStrategy;
import org.nutz.dao.cache.ObsArgClass;
import org.nutz.dao.convent.utils.CommonUtils;

/**
 * @author liaohongliu
 *
 * 创建时间: 2011-4-21
 */
public class FetchMethodHandler implements IDaoCacheMethodHandler{

	public Object handler(ObsArgClass msg) {
		ObsArgClass obsArg=(ObsArgClass) msg;
		//得到调用的参数
		Object[] args=obsArg.getArgs();
		//调用的方法
		Method method=obsArg.getMethod();
		//第一个参数
		Object firstArg=obsArg.getArgs()[0];
		//得到key的工具
		CacheStrategy cacheUtils=obsArg.getCacheStrategy();
		//nutzDao
		Dao dao=cacheUtils.getDao();
		//cache
		Cache cache=obsArg.getCache();
		if(firstArg.getClass()!=String.class){
			Object key=null;
			if(firstArg instanceof Class){
				key=cacheUtils.getKey((Class) firstArg, args);
			}else{
				key=cacheUtils.getKey(firstArg);
			}
			if(key==null){
				return CommonUtils.invokeMethod(method, dao, args);
			}
			//去缓存中取
			Object obj=cache.get(key);
			if(obj!=null){
				return obj;
			}
			obj=CommonUtils.invokeMethod(method, dao, args);
			cache.put(key, obj);
			return obj;
		}
		return CommonUtils.invokeMethod(method, dao, args);
	}

}
