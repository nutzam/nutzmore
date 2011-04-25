/**
 * 
 */
package org.nutz.dao.cache.method;

import org.nutz.dao.Condition;
import org.nutz.dao.cache.ObsArgClass;
import org.nutz.dao.convent.utils.CommonUtils;

/**
 * @author liaohongliu
 *
 * 创建时间: 2011-4-25
 */
public class ClearMethodHandler implements IDaoCacheMethodHandler {

	/* (non-Javadoc)
	 * @see org.nutz.dao.cache.method.IDaoCacheMethodHandler#handler(org.nutz.dao.cache.ObsArgClass)
	 */
	public Object handler(ObsArgClass msg) {
		Object returnValue=CommonUtils.invokeMethod(msg.getMethod(), msg.getCacheStrategy().getDao(), msg.getArgs());
		Object[] args=msg.getArgs();
		if(args.length==1){
			//int clear(Class<?> classOfT);\
			if(args[0].getClass()==Class.class){
				UpdateMethodHandler.removeRelationObjByClassName(msg, ((Class)args[0]).getName());
			}
			//int clear(String tableName);
			if(args[0].getClass()==String.class){
				String tableName=(String) args[0];
				UpdateMethodHandler.removeRelationObj(msg, tableName);
			}
		}
		if(args.length==2){
			//int clear(Class<?> classOfT, Condition condition);
			if(args[0].getClass()==Class.class){
				UpdateMethodHandler.removeRelationObjByClassName(msg, ((Class)args[0]).getName());
			}else
			//int clear(String tableName, Condition condition);
			if(args[0].getClass()==String.class){
				String tableName=(String) args[0];
				UpdateMethodHandler.removeRelationObj(msg, tableName);
			}else{
				//<T> T clearLinks(T obj, String regex);
				Object obj=args[0];
				UpdateMethodHandler.removeRelationObjByClassName(msg, obj.getClass().getName());
			}
		}
		return returnValue;
	}

}
